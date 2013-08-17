package com.Andryyo.ArchPad.archeryFragment;


import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 23.06.13
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
public class CDistanceTemplate implements Serializable{
    private long targetId;
    private int numberOfEnds;
    private int arrowsInEnd;

    public CDistanceTemplate(int numberOfEnds, int arrowsInEnd, long targetId)  {
        this.numberOfEnds = numberOfEnds;
        this.arrowsInEnd = arrowsInEnd;
        this.targetId = targetId;
    }

    public CDistance createDistance(long roundId, long arrowId)   {
        return new CDistance(numberOfEnds, arrowsInEnd, targetId, arrowId,roundId);
    }
}
