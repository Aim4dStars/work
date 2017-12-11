package com.bt.nextgen.core.repository;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "USER_PREFERENCE")
public class UserPreference implements Serializable {

    @EmbeddedId
    private UserPreferenceKey key;

    @Column(name = "VALUE")
    private String value;

    public UserPreference() {
    }

    public UserPreference(UserPreferenceKey key, String value) {
        this.value = value;
        this.key = key;
    }

    public UserPreferenceKey getKey() {
        return key;
    }

    public void setKey(UserPreferenceKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
