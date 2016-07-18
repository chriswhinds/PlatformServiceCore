package com.droitfintech.workflow.internal;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.workflow.exceptions.*;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovySystem;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StatelessScript {

	private static Logger _logger = LoggerFactory.getLogger(StatelessScript.class);
	private String expr;

	public Object execute(Evaluator d, Object it) {
		Object retval = null;
		try {
			retval = this.doExecute(d, it);
		} catch (MissingWorkflowAttributeException ae) {
			throw ae;
		}
		catch (UnknownModuleException ue) {
			throw ue;
		}
		catch (WorkflowClientException wce) {
			throw wce;
		}
		catch (Exception e) {
			throw new WorkflowClientException("The expression '" + expr + "' failed to execute", e);
		}

		if (_logger.isTraceEnabled())
			_logger.trace("Workflow expression ' " + expr + " ' returned value " + retval);

		return retval;
	}

	// Override me
	public abstract Object doExecute(Evaluator d, Object it);

	public static StatelessScript parse(String expr) {

		String source = String.format(scriptBoilerplate, generateScriptName(), "\n", expr, "\n");

		try {
			Class clazz = gcl.parseClass(source);
			// this reduces the memory overhead of the groovy classes without any adverse effect on our runtime needs.
			GroovySystem.getMetaClassRegistry().setMetaClass(clazz, null);
			StatelessScript script =  (StatelessScript) clazz.newInstance();
			script.expr = expr;
			return script;
		} catch (Exception e) {
			throw new WorkflowException("Unable to compile '" + source + "'", e);
		}
	}

	public static String createStatelessScriptWithExpression(String expr) {
		return String.format(scriptBoilerplate, generateScriptName(), "\n", expr, "\n");
	}

	public static int getBoilerpladeLeadingLineCount() { return 1;}

	private static String scriptBoilerplate = ""
			+ "import com.droitfintech.workflow.internal.StatelessScript; "
			+ "import com.droitfintech.workflow.internal.Evaluator;"
			+ "import com.droitfintech.core.regulatory.Tenor;"
			+ "import groovy.time.TimeCategory;"
			+ "class %s extends StatelessScript { "
			+ "    public Object doExecute(Evaluator d, Object it) {%s %s" + "%s    } " + "}";

	private static GroovyClassLoader gcl = new GroovyClassLoader();

	private static int scriptNameCounter;

	private static synchronized String generateScriptName() {
		scriptNameCounter++;
		return "StatelessScript" + scriptNameCounter;
	}

	/*
	 * Convenience methods inside Groovy expressions
	 */
	protected static Map<String, Date> datesCache = new ConcurrentHashMap<String, Date>();

	// Example: "2014-07-07T00:00:00.000-04:00"
	protected static DateTimeFormatter iso8601 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	// Example: "2014-07-07 -0400"
	protected static DateTimeFormatter humanDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd Z");

	// Example: "2014-07-07"
	protected static DateTimeFormatter noTimeZoneDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

	public Date parseDate(Object dateObj) {

		if (dateObj instanceof Date) {
			return (Date) dateObj;
		}

		if (!(dateObj instanceof String)) {
			throw new DroitException("Expected object that's parsable as a date, but got instance of " +
				dateObj.getClass().getCanonicalName());
		}

		String dateString = (String) dateObj;

		if (datesCache.containsKey(dateString)) {
			return datesCache.get(dateString);
		}
		Date date = null;
		try {
			date = humanDateFormat.parseDateTime(dateString).toDate();
		} catch (Exception e) {
			try {
				date = iso8601.parseDateTime(dateString).toDate();
			} catch (Exception ex) {
				try {
					date = noTimeZoneDateFormat.parseDateTime(dateString).toDate();
				} catch (Exception exc) {
					throw new ParseDateWorkflowException("Unable to parse Date '"
						+ dateString + "'", dateString);
				}
			}
		}

		datesCache.put(dateString, date);

		return date;

	}

	private boolean isValidator(Evaluator d) {
		// HACK: Workflow editor validator doesn't have sufficient knowledge of these
		// functions to work properly, and therefore chokes when trying validate expressions
		// containing the approvedVenue function. We try to appease that beast here.
		String s = "com.droitfintech.workflow.editor.GroovyValidator$ValidationEvaluator";
		return s.equals(d.getClass().getName());
	}

	public boolean approvedVenue(Evaluator d, String regulatorName, String venueType, String venueName, Date date) {
		if (isValidator(d)) { return true; }
		StringBuilder b = new StringBuilder();
		b.append(StringUtils.lowerCase(regulatorName));
		b.append(StringUtils.upperCase(venueType));
		b.append("s");
		String bucket = b.toString();
		String name = StringUtils.upperCase(venueName);
		Collection<String> venues = (Collection<String>) d.get("approved_venues").get(bucket);
		return venues.contains(name);
	}

	public static void clearGCLCache() {
		gcl.clearCache();
	}
}
