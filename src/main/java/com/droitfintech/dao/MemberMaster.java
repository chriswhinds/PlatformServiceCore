package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the MemberMaster database table.
 *
 */
@Entity
@Table (name="MemberMaster")
public class MemberMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idMemberMaster")
    private int idMemberMaster;

    @Column (name="memberLEI")
    private String memberLEI;

    @Column (name="memberLegalName")
    private String memberLegalName;

    /**
     * @param memberLEI
     * @param memberLegalName
     */
    public MemberMaster(String memberLEI, String memberLegalName)
    {
        super();
        this.memberLEI = memberLEI;
        this.memberLegalName = memberLegalName;
    }

    public MemberMaster(){

    }

    public int getIdMemberMaster() {
        return idMemberMaster;
    }

    public void setIdMemberMaster(int idMemberMaster) {
        this.idMemberMaster = idMemberMaster;
    }

    public String getMemberLEI() {
        return memberLEI;
    }

    public void setMemberLEI(String memberLEI) {
        this.memberLEI = memberLEI;
    }

    public String getMemberLegalName() {
        return memberLegalName;
    }

    public void setMemberLegalName(String memberLegalName) {
        this.memberLegalName = memberLegalName;
    }
}
