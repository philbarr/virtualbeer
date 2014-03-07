package com.simplyapped.virtualbeer.vuforia;

import com.qualcomm.vuforia.ImageTargetBuilder;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.TrackerManager;
import com.simplyapped.libgdx.ext.vuforia.TargetBuilder;

public class AndroidTargetBuilder implements TargetBuilder {

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

}
