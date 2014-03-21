package com.simplyapped.libgdx.ext.vuforia;

import java.util.Arrays;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.qualcomm.vuforia.Matrix34F;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;

public class AndroidVuforiaTrackableResult implements VuforiaTrackableResult {

	private TrackableResult result;

	public AndroidVuforiaTrackableResult(TrackableResult result) {
		this.result = result;
	}

	@Override
	public Matrix4 getPose() {
		Matrix44F modelViewMatrix = Tool.convertPose2GLMatrix(result.getPose());
		return new Matrix4(modelViewMatrix.getData());
	}


	/**
	 * This gets the position of the camera from the position of the model, basically
	 * inverting the modelview matrix. Not used at the moment
	 * @param cam
	 * @deprecated not needed at the moment
	 */
	@Deprecated
	public void setCameraPositionAndDirection(Camera cam) {
			
		Matrix44F modelViewMatrix = Tool.convertPose2GLMatrix(result.getPose());
		Matrix44F inverseMV = SampleMath.Matrix44FInverse(modelViewMatrix);
		Matrix44F invTranspMV = SampleMath.Matrix44FTranspose(inverseMV);
//		invTranspMV.setData(invertRowMajorToColumnMajor(invTranspMV.getData()));
		
		// Extract the camera position from the last column of the matrix computed before:
		float cam_x = invTranspMV.getData()[12];
		float cam_y = invTranspMV.getData()[13];
		float cam_z = invTranspMV.getData()[14];

		cam.position.set(cam_x, cam_y, cam_z);
		
		// Extract the camera orientation axis (camera viewing direction, camera right direction and camera up direction):
		float cam_right_x = invTranspMV.getData()[0];
		float cam_right_y = invTranspMV.getData()[1];
		float cam_right_z = invTranspMV.getData()[2];
		
		float cam_up_x = -invTranspMV.getData()[4];
		float cam_up_y = -invTranspMV.getData()[5];
		float cam_up_z = -invTranspMV.getData()[6];
		Vector3 rotate = new Vector3(cam_up_x, cam_up_y, cam_up_z).rotate(90, 0, 0, 1);
		cam.up.set(rotate.x, rotate.y, rotate.z);
		 
		float cam_dir_x = invTranspMV.getData()[8];
		float cam_dir_y = invTranspMV.getData()[9];
		float cam_dir_z = invTranspMV.getData()[10];
		cam.direction.set(cam_dir_x, cam_dir_y, cam_dir_z);
		cam.update();
	}

}
