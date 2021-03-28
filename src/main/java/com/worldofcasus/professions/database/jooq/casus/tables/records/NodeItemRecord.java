/*
 * This file is generated by jOOQ.
 */
package com.worldofcasus.professions.database.jooq.casus.tables.records;


import com.worldofcasus.professions.database.jooq.casus.tables.NodeItem;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class NodeItemRecord extends UpdatableRecordImpl<NodeItemRecord> implements Record4<Integer, Integer, byte[], Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>casus.node_item.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>casus.node_item.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>casus.node_item.node_id</code>.
     */
    public void setNodeId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>casus.node_item.node_id</code>.
     */
    public Integer getNodeId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>casus.node_item.item</code>.
     */
    public void setItem(byte[] value) {
        set(2, value);
    }

    /**
     * Getter for <code>casus.node_item.item</code>.
     */
    public byte[] getItem() {
        return (byte[]) get(2);
    }

    /**
     * Setter for <code>casus.node_item.chance</code>.
     */
    public void setChance(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>casus.node_item.chance</code>.
     */
    public Integer getChance() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, Integer, byte[], Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Integer, Integer, byte[], Integer> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return NodeItem.NODE_ITEM.ID;
    }

    @Override
    public Field<Integer> field2() {
        return NodeItem.NODE_ITEM.NODE_ID;
    }

    @Override
    public Field<byte[]> field3() {
        return NodeItem.NODE_ITEM.ITEM;
    }

    @Override
    public Field<Integer> field4() {
        return NodeItem.NODE_ITEM.CHANCE;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public Integer component2() {
        return getNodeId();
    }

    @Override
    public byte[] component3() {
        return getItem();
    }

    @Override
    public Integer component4() {
        return getChance();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public Integer value2() {
        return getNodeId();
    }

    @Override
    public byte[] value3() {
        return getItem();
    }

    @Override
    public Integer value4() {
        return getChance();
    }

    @Override
    public NodeItemRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public NodeItemRecord value2(Integer value) {
        setNodeId(value);
        return this;
    }

    @Override
    public NodeItemRecord value3(byte[] value) {
        setItem(value);
        return this;
    }

    @Override
    public NodeItemRecord value4(Integer value) {
        setChance(value);
        return this;
    }

    @Override
    public NodeItemRecord values(Integer value1, Integer value2, byte[] value3, Integer value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached NodeItemRecord
     */
    public NodeItemRecord() {
        super(NodeItem.NODE_ITEM);
    }

    /**
     * Create a detached, initialised NodeItemRecord
     */
    public NodeItemRecord(Integer id, Integer nodeId, byte[] item, Integer chance) {
        super(NodeItem.NODE_ITEM);

        setId(id);
        setNodeId(nodeId);
        setItem(item);
        setChance(chance);
    }
}
