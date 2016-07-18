package com.droitfintech.model;

import javax.persistence.*;
import java.io.Serializable;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity/Table: MandatoryClearingEnumerationValue
 *
 */
@Entity
@Table(name="MandClearingEnuValue")
public class MandatoryClearingEnumerationValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="TDSS_GEN")
    @Column(name="idMandClearingEnuValue")
    private int idMandatoryClearingEnumerationValue;


    @ManyToOne
    @JoinColumn(name="idMandClearingEnuMatrix")
    private MandatoryClearingEnumerationMatrix mandatoryClearingEnumerationMatrix;

    @Column(name="mandClearingEnuValue")
    private String mandatoryClearingEnumerationValue;

    public MandatoryClearingEnumerationValue() {

    }

    /**
     * @param mandatoryClearingEnumerationMatrix
     * @param mandatoryClearingEnumerationValue
     */
    public MandatoryClearingEnumerationValue(MandatoryClearingEnumerationMatrix mandatoryClearingEnumerationMatrix,
                                             String mandatoryClearingEnumerationValue)
    {
        this.mandatoryClearingEnumerationMatrix = mandatoryClearingEnumerationMatrix;
        this.mandatoryClearingEnumerationValue = mandatoryClearingEnumerationValue;
    }


    /**
     * @return the idMandatoryClearingEnumerationValue
     */
    public int getIdMandatoryClearingEnumerationValue()
    {
        return idMandatoryClearingEnumerationValue;
    }


    /**
     * @param idMandatoryClearingEnumerationValue the idMandatoryClearingEnumerationValue to set
     */
    public void setIdMandatoryClearingEnumerationValue(int idMandatoryClearingEnumerationValue)
    {
        this.idMandatoryClearingEnumerationValue = idMandatoryClearingEnumerationValue;
    }



    /**
     * @return the mandatoryClearingEnumerationMatrix
     */
    public MandatoryClearingEnumerationMatrix getMandatoryClearingEnumerationMatrix()
    {
        return mandatoryClearingEnumerationMatrix;
    }


    /**
     * @param mandatoryClearingEnumerationMatrix the mandatoryClearingEnumerationMatrix to set
     */
    public void setMandatoryClearingEnumerationMatrix(MandatoryClearingEnumerationMatrix mandatoryClearingEnumerationMatrix)
    {
        this.mandatoryClearingEnumerationMatrix = mandatoryClearingEnumerationMatrix;
    }


    /**
     * @return the mandatoryClearingEnumerationValue
     */
    public String getMandatoryClearingEnumerationValue()
    {
        return mandatoryClearingEnumerationValue;
    }


    /**
     * @param mandatoryClearingEnumerationValue the mandatoryClearingEnumerationValue to set
     */
    public void setMandatoryClearingEnumerationValue(String mandatoryClearingEnumerationValue)
    {
        this.mandatoryClearingEnumerationValue = mandatoryClearingEnumerationValue;
    }
}
