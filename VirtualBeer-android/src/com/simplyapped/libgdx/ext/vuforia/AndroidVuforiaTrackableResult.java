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
		Matrix34F pose = result.getPose();
		Matrix3 rotation = new Matrix3();
		rotation.val = new float[]{
				pose.getData()[0], pose.getData()[1], pose.getData()[2],
				pose.getData()[4], pose.getData()[5], pose.getData()[6],
				pose.getData()[8], pose.getData()[9], pose.getData()[10],
		};
		rotation.val = new float[]{
				pose.getData()[0], pose.getData()[4], pose.getData()[8],
				pose.getData()[1], pose.getData()[5], pose.getData()[9],
				pose.getData()[2], pose.getData()[6], pose.getData()[10],
		};
		rotation.rotate(180);
		
		Matrix4 yet = new Matrix4();
//		yet.set(new float[]{
//				three.val[0], three.val[3], three.val[6], 0,
//				three.val[1], three.val[4], three.val[7], 0,
//				three.val[2], three.val[5], three.val[8], 0,
//				pose.getData()[3], pose.getData()[7], pose.getData()[11],1
//		});
		yet.set(new float[]{
				rotation.val[0], rotation.val[1], rotation.val[2], 0,
				rotation.val[3], rotation.val[4], rotation.val[5], 0,
				rotation.val[6], rotation.val[7], rotation.val[8], 0,
				-pose.getData()[7], pose.getData()[3], pose.getData()[11],1
		});
		
		float[] copyOf = Arrays.copyOf(rotation.val, 16);
		copyOf[12] = pose.getData()[3];
		copyOf[13] = pose.getData()[7];
		copyOf[14] = pose.getData()[11];
		copyOf[15] = 1f;
		
		Matrix44F m = Tool.convertPose2GLMatrix(pose);
//		return new Matrix4(invertRowMajorToColumnMajor(m.getData()));
//		float[] m4 = toM4(pose);
		float[] data = m.getData();
		float[] invertRowMajorToColumnMajor = invertRowMajorToColumnMajor(data);
		Matrix4 matrix4 = new Matrix4(invertRowMajorToColumnMajor);
//		Matrix4 matrix42 = new Matrix4(data);
		Matrix4 matrix42 = new Matrix4(copyOf);
//		return matrix4.rotate(0, 1, 0, 180);
//		return matrix4;
		return new Matrix4(data);
//		return yet;
	}

	private float[] toM4(Matrix34F matrix)
	{
		float[] m4 = Arrays.copyOf(matrix.getData(), 16);
		m4[15] = 1;
		return m4;
	}
	
	private float[] invertRowMajorToColumnMajor(float[] rowMajor)
	{
		return new float[]
		{
			rowMajor[0], rowMajor[1], rowMajor[2], rowMajor[3],
			rowMajor[4], rowMajor[5], rowMajor[6], rowMajor[7],
			rowMajor[8], rowMajor[9], rowMajor[10], rowMajor[11],
			rowMajor[12], rowMajor[13], rowMajor[14], rowMajor[15],
		};
	}
	
	private float[] invertRowMajorToColumnMajor2(float[] rowMajor)
	{
		return new float[]
		{
			1,0,0,0,
			0,1,0,0,
			0,0,1,0,
			rowMajor[13], -rowMajor[12], rowMajor[14], 1,
		};
	}	
	
	@Override
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
