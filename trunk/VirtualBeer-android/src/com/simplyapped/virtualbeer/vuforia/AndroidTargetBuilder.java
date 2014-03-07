package com.simplyapped.virtualbeer.vuforia;

import android.util.Log;

import com.qualcomm.vuforia.ImageTargetBuilder;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.TrackerManager;
import com.simplyapped.libgdx.ext.vuforia.TargetBuilder;

public class AndroidTargetBuilder implements TargetBuilder {

	private int targetBuilderCounter;

	@Override
	public boolean startScan() {
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) (trackerManager
            .getTracker(ImageTracker.getClassType()));
        if (imageTracker != null)
        {
            ImageTargetBuilder targetBuilder = imageTracker
                .getImageTargetBuilder();
            
            if (targetBuilder != null)
            {
                // if needed, stop the target builder
                if (targetBuilder.getFrameQuality() != ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_NONE)
                    targetBuilder.stopScan();
                
                imageTracker.stop();
                
                targetBuilder.startScan();
                
            }
        } else
            return false;
        
        return true;
	}

	@Override
	public void stopScan() {
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) (trackerManager
            .getTracker(ImageTracker.getClassType()));
        if (imageTracker != null)
        {
            ImageTargetBuilder targetBuilder = imageTracker
                .getImageTargetBuilder();
            if (targetBuilder != null
                && (targetBuilder.getFrameQuality() != ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_NONE))
            {
                targetBuilder.stopScan();
            }
        }
	}

	@Override
	public boolean isRunning() {
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        
        if (imageTracker != null)
        {
            ImageTargetBuilder targetBuilder = imageTracker
                .getImageTargetBuilder();
            if (targetBuilder != null)
            {
                return (targetBuilder.getFrameQuality() != ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_NONE) ? true
                    : false;
            }
        }
        
        return false;
	}

	@Override
	public int frameQuality() {
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        
        if (imageTracker != null)
        {
            ImageTargetBuilder targetBuilder = imageTracker
                .getImageTargetBuilder();
            if (targetBuilder != null)
            {
            	switch (targetBuilder.getFrameQuality())
            	{
	            	case ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_LOW:
	            		return FRAME_QUALITY_LOW;
	            	case ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_MEDIUM:
	            		return FRAME_QUALITY_MEDIUM;
	            	case ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_HIGH:
	            		return FRAME_QUALITY_HIGH;
            		default:
            			return FRAME_QUALITY_NONE;
            	}
            }
        }
        return FRAME_QUALITY_NONE;
	}

    @Override
	public boolean build(String name, float sceneSizeWidth)
    {
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        
        if (imageTracker != null)
        {
            ImageTargetBuilder targetBuilder = imageTracker
                .getImageTargetBuilder();
            if (targetBuilder != null)
            {
                if (targetBuilder.getFrameQuality() != ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_NONE)
                {
	                return targetBuilder.build(name, sceneSizeWidth);
                }
            }
        }
		return false;
    }
}
