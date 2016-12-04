package com.socnet.data;

import java.util.Date;
import java.util.UUID;

/**
 *
 * @author fallgamlet
 */
public abstract class BaseItem {

    private String mUUID;
    private Date mCreated;
    private Date mUpdated;
    private Date mSynced;

    public static final String F_UUID = "uuid";
    public static final String F_CREATED = "created";
    public static final String F_UPDATED = "updated";
    public static final String F_SYNCED = "synced";
    
    
    public String getUUID() { return mUUID; }
    public void setUUID(String uuid) { mUUID = uuid; }
    
    public Date getCreated() { return mCreated; }
    public void setCreated(Date date) { mCreated = date; }
    
    public Date getUpdated() { return mUpdated; }
    public void setUpdated(Date date) { mUpdated = date; }
    
    public Date getSynced() { return mSynced; }
    public void setSunced(Date date) { mSynced = date; }

    @Override
    public String toString() {
        String uuid = getUUID();
        return uuid==null? super.toString(): uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof BaseItem)) { return false; }
        String uuid = getUUID();
        String objUUID = ((BaseItem) obj).getUUID();
        return (uuid == null ? objUUID == null : uuid.equals(objUUID));
    }

    @Override
    public int hashCode() {
        String uuid = getUUID();
        return uuid == null? super.hashCode(): uuid.hashCode();
    }
        
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
    
    

}
