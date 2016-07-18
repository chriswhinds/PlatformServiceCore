package com.droitfintech.dao;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.droitfintech.model.DeepCopiable;
import com.droitfintech.model.MasterBase;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.droitfintech.model.DeepCopiable;
import com.droitfintech.model.DeepCopyIgnore;
import com.droitfintech.model.DeepCopyIgnore.Type;

/**
 *
 * @author Saurabh Sirdeshmukh
 * The entity class for the table: ClearingMandatePhase
 */

@Entity
@Table (name="ClearingMandatePhase")
public class ClearingMandatePhase implements Serializable, DeepCopiable<ClearingMandatePhase>
{
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idClearingMandatePhase")
    @DeepCopyIgnore(type=Type.NULL)
    private int idClearingMandatePhase;

    @ManyToOne
    @JoinColumn (name="idMasterBase")
    @DeepCopyIgnore(type=Type.NULL)
    private MasterBase masterBase;

    @Column (name="categorizationField")
    private String categorizationField;

    @Column (name="categorizationValue")
    private String categorizationValue;

    @Column (name="effectiveDate")
    @Temporal(TemporalType.DATE)
    private Date effectiveDate;

    public ClearingMandatePhase() {
    }

    public ClearingMandatePhase(MasterBase master) {
        master.addClearingMandatePhase(this);
    }

    public int getIdClearingMandatePhase()
    {
        return idClearingMandatePhase;
    }

    public void setIdClearingMandatePhase(int idClearingMandatePhase)
    {
        this.idClearingMandatePhase = idClearingMandatePhase;
    }

    @JsonIgnore
    public MasterBase getMasterBase()
    {
        return masterBase;
    }

    public void setMasterBase(MasterBase masterBase)
    {
        this.masterBase = masterBase;
    }

    public String getCategorizationField()
    {
        return categorizationField;
    }


    public void setCategorizationField(String categorizationField)
    {
        this.categorizationField = categorizationField;
    }


    public String getCategorizationValue()
    {
        return categorizationValue;
    }


    public void setCategorizationValue(String categorizationValue)
    {
        this.categorizationValue = categorizationValue;
    }


    public Date getEffectiveDate()
    {
        return effectiveDate;
    }


    public void setEffectiveDate(Date effectiveDate)
    {
        this.effectiveDate = effectiveDate;
    }

}
