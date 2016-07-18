package com.droitfintech.workflow.internal.repository;

import com.droitfintech.utils.BucketedMap;
import com.droitfintech.exceptions.DroitException;
import com.droitfintech.fx.FxConverter;
import com.droitfintech.workflow.exceptions.UnknownModuleException;
import com.droitfintech.workflow.exceptions.WorkflowClientException;
import com.droitfintech.workflow.exceptions.WorkflowException;
import com.droitfintech.workflow.internal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *  Create by Chris Hinds fro Core OldBox code base
 */
public class WorkflowEvaluator implements Evaluator {

    private ExecutableSnapshot executableSnapshot;
    private FxConverter fxConverter;
    private BucketedMap payload;
    private boolean collectEscalations;
    private Stack<String> lastCollectionNameStack = new Stack<String>();
    private String lastCollectionAccessed = "";

    private static Logger log = LoggerFactory.getLogger(Evaluator.class);

    public WorkflowEvaluator(BucketedMap inputData, ExecutableSnapshot executableSnapshot,
                             FxConverter fxConverter,
                             boolean strictAttributeChecking, boolean collectEscalations) {

        this.payload = inputData;
        this.executableSnapshot = executableSnapshot;
        this.fxConverter = fxConverter;
        this.collectEscalations = collectEscalations;

        if (payload == null) {
            throw new WorkflowException("Cannot create a Decision object with a null payload");
        }

        // convert all buckets to 'strict' buckets, meaning that the workflow will fail
        // if it can't find a value on a bucket.
        if (strictAttributeChecking) {

            Set<String> buckets = payload.getBuckets();

            for (String bucketName : buckets) {

                Map<String, Object> bucket = payload.getAllValuesForBucket(bucketName);
                Map<String, Object> strictBucket = new WorkflowStrictMapDecorator<String,Object>(bucket, bucketName);
                payload.setAllValuesForBucket(bucketName, strictBucket);

            }
        }
    }

    public WorkflowEvaluator(BucketedMap inputData, ExecutableSnapshot executableSnapshot,
                             boolean strictAttributeCechking, boolean collectEscalations) {
        new WorkflowEvaluator(inputData, executableSnapshot, null, strictAttributeCechking, collectEscalations);
    }

    public BucketedMap getAll(String moduleName) {

        // Ensure everything has already been run
        this.get(moduleName);
        return payload;
    }

    public BucketedMap getPayload() {
        return payload;
    }


    public String getLastCollectionAccessed() {
        return lastCollectionAccessed;
    }

    public Map<String, Object> get(String moduleName) {
        lastCollectionAccessed = moduleName;
        if (!payload.getBuckets().contains(moduleName)) {
            lastCollectionNameStack.push(lastCollectionAccessed);
            if (log.isTraceEnabled())
                log.trace("Evaluating module: " + moduleName);

            // If workflow hasn't been computed yet, do so now
            ExecutableModule module = executableSnapshot.getModule(moduleName);
            if (module == null) {
                throw new UnknownModuleException(String.format("Module '%s' not found", moduleName), moduleName);
            }
            payload.setAllValuesForBucket(moduleName, null); // Pre-order
            // traversal
            WorkflowStrictMap<String, Object> workflowResults = null;
            try {
                workflowResults = module.execute(this, collectEscalations);
                lastCollectionAccessed = lastCollectionNameStack.pop();
                if (log.isTraceEnabled())
                    log.trace("Workflow results for module '" + moduleName + "':" + workflowResults);
            } catch(WorkflowClientException wce) {
                throw wce;
            } catch (Exception e) {
                // Throwing a Client exception because, while it might actually be a real technical problem it's
                // more likely that there's an issue with their incoming data.
                throw new WorkflowClientException("Could not run workflow for module " + module.getVersionMetadata().getId(), module.getName(), e);
            }

            payload.setAllValuesForBucket(moduleName, workflowResults);
        }
        return payload.getAllValuesForBucket(moduleName);
    }


    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fxConverter == null) {
            throw new DroitException("FX Conversion requested but no FX Converter available.");
        }
        return fxConverter.convert(fromCurrency, toCurrency, amount, new Date()).getResult();
    }
}
