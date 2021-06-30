package com.abc.alarmclockplus.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abc.alarmclockplus.databinding.FragmentHomeBinding;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class HomeFragment extends Fragment
{
   private HomeViewModel homeViewModel;
   private FragmentHomeBinding binding;
   private JSONObject homeFragmentSave;

   public static Uri ringtoneUri;
   public static Boolean IsVibrateON = false;

   private void JSONfileWriter(JSONObject JO, String fileName)
   {
/*      try (FileWriter file = new FileWriter("" + fileName + ".json")) {
         //We can write any JSONArray or JSONObject instance to the file
         file.write(String.valueOf(JO));
         file.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }*/

      try {
         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
         writer.writeValue(Paths.get(String.valueOf(Uri.fromFile(new File("/storage/emulated/0/Download/com.abc.alarmclockplus/JSONs/" + fileName + ".json")))).toFile(), JO);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private JSONObject JSONfileReader(String fileName)
   {
/*      try (FileReader reader = new FileReader(fileName + ".json"))
      {
         return (JSONObject) (Object) reader;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;*/

      /*try {
         // create object mapper instance
         ObjectMapper mapper = new ObjectMapper();
         return mapper.readValue(Paths.get("/storage/emulated/0/Download/com.abc.alarmclockplus/JSONs/" + fileName + ".json").toFile(), JSONObject.class);
      } catch (Exception ex) {
         ex.printStackTrace();
         return null;
      }*/

      try {
         // create Gson instance
         Gson gson = new Gson();
         // create a reader
         Reader reader = Files.newBufferedReader(Paths.get("/storage/emulated/0/Download/com.abc.alarmclockplus/JSONs/" + fileName + ".json"));
         // convert JSON file to map
         Map<String, ?> map = gson.fromJson(reader, Map.class);
         reader.close();
         JSONObject JO = new JSONObject();
         JO.put("IsVibrateON", map.get("IsVibrateON"));
         JO.put("RingtoneUri", map.get("RingtoneUri"));

         return JO;
      } catch (Exception ex) {
         ex.printStackTrace();
         return null;
      }
   }

   private void JSONsaverCreate()
   {
      homeFragmentSave = new JSONObject();
      try {
         homeFragmentSave.put("IsVibrateON", true);
         homeFragmentSave.put("RingtoneUri", null);
         JSONfileWriter(homeFragmentSave, "homeFragmentSave");
      } catch (JSONException e) {
         e.printStackTrace();
      }
   }

   private void JSONsaver(String ID, Object Value)
   {
      try {
         homeFragmentSave.put(ID, Value);
      } catch (JSONException e) {
         e.printStackTrace();
      }
   }

   private void JSONrestore(String ID, Object Value)
   {
      try {
         homeFragmentSave.put(ID, Value);
      } catch (JSONException e) {
         e.printStackTrace();
      }
   }

   private void JSONrestoreAll()
   {
      try {
         homeFragmentSave = JSONfileReader("homeFragmentSave");
         if(homeFragmentSave != null)
         {
            if(binding.switchVibrateAlways != null)
               IsVibrateON = (Boolean) homeFragmentSave.get("IsVibrateON");
            if(binding.buttonChooseRingtone != null)
            {
               ringtoneUri = (Uri) homeFragmentSave.get("RingtoneUri");
               if(ringtoneUri != null)
                  binding.buttonChooseRingtone.setText(getFileName(ringtoneUri));
            }

            if(binding.buttonChooseRingtone != null)
               binding.buttonChooseRingtone.setText(getFileName(ringtoneUri));
         }

      } catch (JSONException e) {
         e.printStackTrace();
      }
   }

   public void play(Uri uri)
   {
      Intent intent = new Intent();
      intent.setAction(android.content.Intent.ACTION_VIEW);
      intent.setDataAndType(uri, "audio/*");
      startActivity(intent);
   }

   public String getFileName(Uri uri)
   {
      String result = null;
      if (uri.getScheme().equals("content"))
      {
         try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)){
            if (cursor != null && cursor.moveToFirst())
               result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
         }
      }
      if (result == null)
      {
         result = uri.getPath();
         int cut = result.lastIndexOf('/');
         if (cut != -1)
         {
            result = result.substring(cut + 1);
         }
      }
      return result;
   }

   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);
      homeViewModel =
         new ViewModelProvider(this).get(HomeViewModel.class);

      binding = FragmentHomeBinding.inflate(inflater, container, false);
      View root = binding.getRoot();

      final TextView textClock = binding.textClock;
      final TextView textDate = binding.textDate;
      final Chronometer chronometer = binding.chronometer;
      final ProgressBar progressBarSeconds = binding.progressBarSeconds;
      final FrameLayout frameLayoutSettingsView = binding.frameLayoutSettingsView;
      final Button buttonSettingsViewToggle = binding.buttonSettingsViewToggle;
      final Button chooseRingtoneButton = binding.buttonChooseRingtone;
      final Switch switchVibrateAlways = binding.switchVibrateAlways;

      JSONsaverCreate();
      if(homeFragmentSave != null)
      {
         JSONrestoreAll();
         Toast.makeText(this.getContext(), "restored from JSON", Toast.LENGTH_SHORT).show();
      }
      else
         Toast.makeText(this.getContext(), "not restored from JSON", Toast.LENGTH_SHORT).show();

      buttonSettingsViewToggle.setOnClickListener(L -> {
         if (frameLayoutSettingsView.getVisibility() == View.GONE)
            frameLayoutSettingsView.setVisibility(View.VISIBLE);
         else
         {
            frameLayoutSettingsView.setVisibility(View.GONE);
         }
      });

      chooseRingtoneButton.setOnClickListener(R -> {
         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
         intent.setType("audio/*");
         intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
         startActivityForResult(Intent.createChooser(intent, "Music File"), 200);
         ringtoneUri = intent.getData();
         chooseRingtoneButton.setText(getFileName(ringtoneUri));
      });

      switchVibrateAlways.setOnClickListener(C -> {
         if(switchVibrateAlways.isChecked())
            IsVibrateON = true;
         else
            IsVibrateON = false;
      });

      chronometer.setOnChronometerTickListener(chr -> {
         textClock.setText(DateFormat.format("k:mm:ss", new java.util.Date()));
         progressBarSeconds.setProgress(Integer.parseInt((String) DateFormat.format("ss", new java.util.Date())), true);
      });

      homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
      {
         @Override
         public void onChanged(@Nullable String s)
         {
            textDate.setText(DateFormat.format("dd-MM-yyyy", new java.util.Date()));
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
         }

      });

      return root;
   }

   @Override
   public void onDestroyView()
   {
      super.onDestroyView();
      binding = null;
   }
}