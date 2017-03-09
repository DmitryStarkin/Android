package com.hplasplas.task6.Util;

/**
 * Created by StarkinDG on 09.03.2017.
 */

public class intQueue {
    
    private final double DEFAULT_RESIZE_FACTOR = 0.5;
    private final int DEFAULT_SIZE = 16;
    private int[] values;
    private double resizeFactor;
    private int headIndex = 0;
    private int tailIndex = -1;
    private int size = 0;
    
    public intQueue() {
        
        values = new int[DEFAULT_SIZE];
        resizeFactor = DEFAULT_RESIZE_FACTOR;
    }
    
    public intQueue(int size) {
        
        values = new int[size];
        resizeFactor = DEFAULT_RESIZE_FACTOR;
    }
    
    public intQueue(int size, double resizeFactor) {
        
        values = new int[size];
        this.resizeFactor = resizeFactor;
    }
    
    public void add(int value) {
        if(size == values.length){
            resize();
        }
        tailIndex++;
        if(tailIndex == values.length){
            tailIndex = 0;
        }
        values[tailIndex] = value;
        size++;
    }
    
    public int poll() {
        
        if (isEmpty()) {
            throw new IllegalStateException("Queue Empty");
        }
        int value = values[headIndex];
        size--;
        if (headIndex == tailIndex) {
            tailIndex = -1;
            headIndex = 0;
        } else if (++headIndex == values.length) {
            headIndex = 0;
        }
        return value;
    }
    
    public int peek() {
        
        if (isEmpty()) {
            throw new IllegalStateException("Queue Empty");
        }
        return values[headIndex];
    }
    
    public int size() {
        
        return size;
    }
    
    public boolean isEmpty() {
        
        return size() == 0;
    }
    
    private void resize() {
        
        int[] newSizeValues = new int[(int)(values.length + values.length * resizeFactor)];
        for (int i = 0, y = headIndex; i < size; i++, y++){
            if (y == values.length){
                y = 0;
            }
            newSizeValues[i] = values[y];
        }
        headIndex = 0;
        tailIndex = size - 1;
        values = newSizeValues;
    }
}
