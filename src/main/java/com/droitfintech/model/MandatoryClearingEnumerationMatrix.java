package com.droitfintech.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

/**
 * Entity implementation class for Entity/Table: MandatoryClearingEnumerationMatrix
 *
 */
@Entity
@Table(name="MandClearingEnuMatrix")
public class MandatoryClearingEnumerationMatrix implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column(name="idMandClearingEnuMatrix")
    private int idMandatoryClearingEnumerationMatrix;

    @ManyToOne
    @JoinColumn(name="idFinMktInfraVersion")
    private FinMktInfraVersion clearingMandateVersion;

    @ManyToOne
    @JoinColumn(name="idMandClearingEnuField")
    private MandatoryClearingEnumerationField mandatoryClearingEnumerationField;

    @OneToMany(mappedBy = "mandatoryClearingEnumerationMatrix")
    private Set<MandatoryClearingEnumerationValue> mandatoryClearingEnumerationValueSet;


    public MandatoryClearingEnumerationMatrix() {

    }


    /**
     * @param clearingMandateVersion
     * @param mandatoryClearingEnumerationField
     */
    public MandatoryClearingEnumerationMatrix(FinMktInfraVersion clearingMandateVersion,
                                              MandatoryClearingEnumerationField mandatoryClearingEnumerationField)
    {
        this.clearingMandateVersion = clearingMandateVersion;
        this.mandatoryClearingEnumerationField = mandatoryClearingEnumerationField;
    }


    /**
     * @return the idMandatoryClearingEnumerationMatrix
     */
    public int getIdMandatoryClearingEnumerationMatrix()
    {
        return idMandatoryClearingEnumerationMatrix;
    }



    /**
     * @param idMandatoryClearingEnumerationMatrix the idMandatoryClearingEnumerationMatrix to set
     */
    public void setIdMandatoryClearingEnumerationMatrix(int idMandatoryClearingEnumerationMatrix)
    {
        this.idMandatoryClearingEnumerationMatrix = idMandatoryClearingEnumerationMatrix;
    }


    /**
     * @return the clearingMandateVersion
     */
    public FinMktInfraVersion getClearingMandateVersion()
    {
        return clearingMandateVersion;
    }



    /**
     * @param clearingMandateVersion the clearingMandateVersion to set
     */
    public void setClearingMandateVersion(FinMktInfraVersion clearingMandateVersion)
    {
        this.clearingMandateVersion = clearingMandateVersion;
    }



    /**
     * @return the mandatoryClearingEnumerationField
     */
    public MandatoryClearingEnumerationField getMandatoryClearingEnumerationField()
    {
        return mandatoryClearingEnumerationField;
    }



    /**
     * @param mandatoryClearingEnumerationField the mandatoryClearingEnumerationField to set
     */
    public void setMandatoryClearingEnumerationField(MandatoryClearingEnumerationField mandatoryClearingEnumerationField)
    {
        this.mandatoryClearingEnumerationField = mandatoryClearingEnumerationField;
    }

    /**
     * @return the mandatoryClearingEnumerationValueSet
     */
    public Set<MandatoryClearingEnumerationValue> getMandatoryClearingEnumerationValueSet()
    {
        return mandatoryClearingEnumerationValueSet;
    }



    /**
     * @param mandatoryClearingEnumerationValueSet the mandatoryClearingEnumerationValueSet to set
     */
    public void setMandatoryClearingEnumerationValueSet(
            Set<MandatoryClearingEnumerationValue> mandatoryClearingEnumerationValueSet)
    {
        this.mandatoryClearingEnumerationValueSet = mandatoryClearingEnumerationValueSet;
    }


    public MandatoryClearingEnumerationValue addMandatoryClearingEnumerationValue(MandatoryClearingEnumerationValue mandatoryClearingEnumerationValue) {
        getMandatoryClearingEnumerationValueSet().add(mandatoryClearingEnumerationValue);
        mandatoryClearingEnumerationValue.setMandatoryClearingEnumerationMatrix(this);

        return mandatoryClearingEnumerationValue;
    }

    public MandatoryClearingEnumerationValue removeMandatoryClearingEnumerationValue(MandatoryClearingEnumerationValue mandatoryClearingEnumerationValue) {
        getMandatoryClearingEnumerationValueSet().remove(mandatoryClearingEnumerationValue);
        mandatoryClearingEnumerationValue.setMandatoryClearingEnumerationMatrix(null);

        return mandatoryClearingEnumerationValue;
    }

}
