package com.droitfintech.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity/Table: MandatoryClearingEnumerationField
 *
 */
@Entity
@Table(name="MandClearingEnuField")
public class MandatoryClearingEnumerationField implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column(name="idMandClearingEnuField")
    private int idMandatoryClearingEnumerationField;

    @Column(name="MandClearingEnuFieldName")
    private String mandatoryClearingEnumerationFieldName;

    public MandatoryClearingEnumerationField() {

    }

    public MandatoryClearingEnumerationField(String mandatoryClearingEnumerationFieldName) {
        this.mandatoryClearingEnumerationFieldName = mandatoryClearingEnumerationFieldName;
    }


    /**
     * @return the idMandatoryClearingEnumerationField
     */
    public int getIdMandatoryClearingEnumerationField()
    {
        return idMandatoryClearingEnumerationField;
    }


    /**
     * @param idMandatoryClearingEnumerationField the idMandatoryClearingEnumerationField to set
     */
    public void setIdMandatoryClearingEnumerationField(int idMandatoryClearingEnumerationField)
    {
        this.idMandatoryClearingEnumerationField = idMandatoryClearingEnumerationField;
    }


    /**
     * @return the mandatoryClearingEnumerationFieldName
     */
    public String getMandatoryClearingEnumerationFieldName()
    {
        return mandatoryClearingEnumerationFieldName;
    }


    /**
     * @param mandatoryClearingEnumerationFieldName the mandatoryClearingEnumerationFieldName to set
     */
    public void setMandatoryClearingEnumerationFieldName(String mandatoryClearingEnumerationFieldName)
    {
        this.mandatoryClearingEnumerationFieldName = mandatoryClearingEnumerationFieldName;
    }

}
