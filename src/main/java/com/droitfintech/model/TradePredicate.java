package com.droitfintech.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droitfintech.model.TradeContext;
import com.droitfintech.model.Validatable;
import com.droitfintech.model.ValidationResult;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Predicate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "predicateType", discriminatorType = DiscriminatorType.STRING)
@XmlTransient
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="class")

public abstract class TradePredicate implements Predicate<TradeContext>, Comparable<TradePredicate>,
        Validatable{

    private static Logger log = LoggerFactory.getLogger(TradePredicate.class);

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    protected Integer idTradePredicate;

    @OneToMany(orphanRemoval=true, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "TradePredicateRelationship", joinColumns = @JoinColumn(name = "idParentPredicate"),
            inverseJoinColumns = @JoinColumn(name = "idChildPredicate"))
    @Fetch(value = FetchMode.SUBSELECT)
    @Sort(type = SortType.NATURAL)
    protected SortedSet<TradePredicate> children;

    @Transient
    private int generatedHashCode = Integer.MIN_VALUE;

    public TradePredicate() {
        ;
    }

    public TradePredicate(TradePredicate... children) {
        for (TradePredicate child: children) {
            addChild(child);
        }
    }

    @XmlTransient
    @JsonIgnore
    public Integer getIdTradePredicate() {
        return idTradePredicate;
    }

    public void setIdTradePredicate(Integer idTradePredicate) {
        this.idTradePredicate = idTradePredicate;
    }

    @XmlAnyElement
    public Set<TradePredicate> getChildren() {
        if (children == null) {
            children = new TreeSet<TradePredicate>();
        }
        return children;
    }

    public void addChild(TradePredicate child) {
        getChildren().add(child);
    }

    public abstract TradePredicate copy();


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TradePredicate other = (TradePredicate) obj;
        if (children == null) {
            if (other.children != null)
                return false;
        } else if (!children.equals(other.children))
            return false;
        return true;
    }


    public int compareTo(TradePredicate rhs) {
        return new CompareToBuilder()
                .append(getClass().getName(), rhs.getClass().getName())
                .append(getChildren().toArray(), rhs.getChildren().toArray())
                .toComparison();
    }

    @Override
    public int hashCode() {

        if (generatedHashCode == Integer.MIN_VALUE) {

            synchronized (children) {
                if (generatedHashCode == Integer.MIN_VALUE) {
                    this.generatedHashCode = regenerateHashCode();
                }
            }
        }

        return generatedHashCode;
    }

    private int regenerateHashCode() {

        int hash = new HashCodeBuilder(17, 39).
                append(getClass()).
                append(children).
                toHashCode();

        return hash;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.getChildren() + ")";
    }


    public ValidationResult validate() {
        ValidationResult res = new ValidationResult();
        for (TradePredicate child: children) {
            ValidationResult childResult = child.validate();
            if (!childResult.isValid) {
                res.isValid = false;
                res.addAllExceptions(childResult.getExceptions());
            }
        }
        return res;
    }

    /**
     * Returns 1 if the predicate has no children; else, it returns the combined size of all of its children.
     *
     * @return the combined size of this predicate's children, if any; else 1.
     */
    public int size() {
        if (children == null || children.size() == 0) {
            return 1;
        }
        int res = 0;
        for (TradePredicate child: children) {
            res = res + child.size();
        }
        return res;
    }

    @JsonIgnore
    public Set<String> getAttributeSignature() {
        Set<String> res = new HashSet<String>();
        for (TradePredicate child: children) {
            res.addAll(child.getAttributeSignature());
        }
        return res;
    }

    /**
     * Return itself + a scan of all descendents, depth-first, as a List.
     *
     * @return
     */
    public List<TradePredicate> listDescendents() {
        List<TradePredicate> res = new LinkedList<TradePredicate>();
        res.add(this);
        if (children != null) {
            for (TradePredicate child: children) {
                res.addAll(child.listDescendents());
            }
        }
        return res;
    }

}
