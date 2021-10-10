/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.demo.CameraXViewModel;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.VisionImageProcessor;
import com.google.mlkit.vision.demo.java.facedetector.FaceDetectorProcessor;
import com.google.mlkit.vision.demo.preference.PreferenceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Live preview demo app for ML Kit APIs using CameraX. */
@KeepName
@RequiresApi(VERSION_CODES.LOLLIPOP)
public final class CameraXLivePreviewActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback {
  private static final String TAG = "CameraXLivePreview";
  private static final int PERMISSION_REQUESTS = 1;

  private static final String STATE_SELECTED_MODEL = "selected_model";

  private PreviewView previewView;
  private GraphicOverlay graphicOverlay;

  @Nullable private ProcessCameraProvider cameraProvider;
  @Nullable private Preview previewUseCase;
  @Nullable private ImageAnalysis analysisUseCase;
  @Nullable private VisionImageProcessor imageProcessor;
  private boolean needUpdateGraphicOverlayImageSourceInfo;

  private String selectedModel = "Face Detection";
  private int lensFacing = CameraSelector.LENS_FACING_FRONT;
  private CameraSelector cameraSelector;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
      Toast.makeText(
              getApplicationContext(),
              "CameraX is only supported on SDK version >=21. Current SDK version is "
                      + VERSION.SDK_INT,
              Toast.LENGTH_LONG)
              .show();
      return;
    }

    if (savedInstanceState != null) {
      selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, selectedModel);
    }
    cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

    setContentView(R.layout.activity_vision_camerax_live_preview);
    previewView = findViewById(R.id.preview_view);
    if (previewView == null) {
      Log.d(TAG, "previewView is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    new ViewModelProvider(this, AndroidViewModelFactory.getInstance(getApplication()))
            .get(CameraXViewModel.class)
            .getProcessCameraProvider()
            .observe(
                    this,
                    provider -> {
                      cameraProvider = provider;
                      if (allPermissionsGranted()) {
                        bindAllCameraUseCases();
                      }
                    });

    Button capture_btn = (Button)findViewById(R.id.capture_btn);
    capture_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        File path = new File(Environment.getExternalStorageDirectory() + "/images/");
        path.mkdirs();

        Bitmap bitmap = previewView.getBitmap();

        if(bitmap == null) {
          Log.e("saveBitmapToJpg","bitmap null");
          return;
        }

        String file_name = "1.jpg";
        File imgFile = new File(path, file_name);

        try {
          FileOutputStream out = new FileOutputStream(imgFile);
          bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out); //썸네일로 사용하므로 퀄리티를 낮게설정
          out.close();
        } catch (FileNotFoundException e) {
          Log.e("saveBitmapToJpg","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
          Log.e("saveBitmapToJpg", "IOException : " + e.getMessage());
        }
      }
    });

    ImageButton info = (ImageButton)findViewById(R.id.info);
    info.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        String MSG =
                "사각 프레임에 맞춰 촬영 버튼을 클릭합니다.";

        AlertDialog show = new AlertDialog.Builder(CameraXLivePreviewActivity.this)
                .setMessage(MSG)
                .setPositiveButton(android.R.string.ok, null)
                .show();
      }
    });

    if (!allPermissionsGranted()) {
      getRuntimePermissions();
    }
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putString(STATE_SELECTED_MODEL, selectedModel);
  }

  @Override
  public void onResume() {
    super.onResume();
    bindAllCameraUseCases();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (imageProcessor != null) {
      imageProcessor.stop();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (imageProcessor != null) {
      imageProcessor.stop();
    }
  }

  private void bindAllCameraUseCases() {
    if (cameraProvider != null) {
      // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
      cameraProvider.unbindAll();
      bindPreviewUseCase();
      bindAnalysisUseCase();
    }
  }

  private void bindPreviewUseCase() {
    if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
      return;
    }
    if (cameraProvider == null) {
      return;
    }
    if (previewUseCase != null) {
      cameraProvider.unbind(previewUseCase);
    }

    Preview.Builder builder = new Preview.Builder();
    Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
    if (targetResolution != null) {
      builder.setTargetResolution(targetResolution);
    }
    previewUseCase = builder.build();
    previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());
    cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, previewUseCase);
  }

  private void bindAnalysisUseCase() {
    if (cameraProvider == null) {
      return;
    }
    if (analysisUseCase != null) {
      cameraProvider.unbind(analysisUseCase);
    }
    if (imageProcessor != null) {
      imageProcessor.stop();
    }

    try {
      Log.i(TAG, "Using Face Detector Processor");
      imageProcessor = new FaceDetectorProcessor(this);

    } catch (Exception e) {
      Log.e(TAG, "Can not create image processor: " + selectedModel, e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getLocalizedMessage(),
              Toast.LENGTH_LONG)
              .show();
      return;
    }

    ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
    Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
    if (targetResolution != null) {
      builder.setTargetResolution(targetResolution);
    }
    analysisUseCase = builder.build();

    needUpdateGraphicOverlayImageSourceInfo = true;
    analysisUseCase.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(this),
            imageProxy -> {
              if (needUpdateGraphicOverlayImageSourceInfo) {
                boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                  graphicOverlay.setImageSourceInfo(
                          imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                } else {
                  graphicOverlay.setImageSourceInfo(
                          imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
                }
                needUpdateGraphicOverlayImageSourceInfo = false;
              }
              try {
                imageProcessor.processImageProxy(imageProxy, graphicOverlay);
              } catch (MlKitException e) {
                Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                        .show();
              }
            });

    cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, analysisUseCase);
  }

  private String[] getRequiredPermissions() {
    try {
      PackageInfo info =
              this.getPackageManager()
                      .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      if (ps != null && ps.length > 0) {
        return ps;
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      return new String[0];
    }
  }

  private boolean allPermissionsGranted() {
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        return false;
      }
    }
    return true;
  }

  private void getRuntimePermissions() {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(
              this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, String[] permissions, int[] grantResults) {
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      bindAllCameraUseCases();
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private static boolean isPermissionGranted(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission granted: " + permission);
      return true;
    }
    Log.i(TAG, "Permission NOT granted: " + permission);
    return false;
  }
}
