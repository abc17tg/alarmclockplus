package com.abc.alarmclockplus.ui.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abc.alarmclockplus.TimeRelatedUtils;
import com.abc.alarmclockplus.databinding.FragmentAlarmBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AlarmFragment extends Fragment
{
    private AlarmViewModel alarmViewModel;
    private FragmentAlarmBinding binding;
    private Button lastClickedButton;

    /*private JSONArray alarmFragmentSaveArrayStartHour;
    private JSONArray alarmFragmentSaveArrayEndHour;
    private JSONArray alarmFragmentSaveArraysNumberSignedOfAlarms;*/

    private void TimePickerView(Boolean setVisible)
    {
        if (setVisible)
            binding.frameLayoutTimePicker.setVisibility(View.VISIBLE);
        else
            binding.frameLayoutTimePicker.setVisibility(View.GONE);
    }

    /*private void JSONsaverCreate()
    {
        alarmFragmentSaveArrayStartHour = new JSONArray();
        alarmFragmentSaveArrayEndHour = new JSONArray();
        alarmFragmentSaveArraysNumberSignedOfAlarms = new JSONArray();

        List<String> buttonsStartHourTexts = new ArrayList<>();
        for (Button buttonStartHour:buttonsStartHour)
        {
            buttonsStartHourTexts.add(buttonStartHour.getText().toString());
        }
        alarmFragmentSaveArrayStartHour.put(buttonsStartHourTexts);

        List<String> buttonsEndHourTexts = new ArrayList<>();
        for (Button buttonEndHour:buttonsEndHour)
        {
            buttonsEndHourTexts.add(buttonEndHour.getText().toString());
        }
        alarmFragmentSaveArrayEndHour.put(buttonsEndHourTexts);

        List<Integer> numberSignedOfAlarms = new ArrayList<>();
        for (EditText editTextNumberSignedOfAlarm:editTextsNumberSignedOfAlarms)
        {
            numberSignedOfAlarms.add(Integer.parseInt(editTextNumberSignedOfAlarm.getText().toString()));
        }
        alarmFragmentSaveArraysNumberSignedOfAlarms.put(numberSignedOfAlarms);
    }*/

    private void JSONsaver(JSONArray JA, List<Object> list)
    {
        JA = new JSONArray();
        JA.put(list);
    }

    private void JSONrestore(List<Object> list, JSONArray JA) throws JSONException
    {
        list = new ArrayList<>();
        for (int i = 0; i<JA.length(); i++)
            list.add(JA.get(i));
    }

    private void AlarmSetter(String startTime, String endTime, int numberOfAlarms, boolean dismissOrSet)
    {
        int startTimeInt = TimeRelatedUtils.ConvertStringTimeToIntegerSeconds(startTime);
        int endTimeInt = TimeRelatedUtils.ConvertStringTimeToIntegerSeconds(endTime);
        List<Integer> TimeList;
        if(numberOfAlarms > 1)
            TimeList = TimeRelatedUtils.ListOfEvenlySpacedHours(startTimeInt, endTimeInt, numberOfAlarms);
        else
        {
            Intent intent;
            if(dismissOrSet)
            {
                intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                //intent.putExtra(AlarmClock.EXTRA_RINGTONE, HomeFragment.ringtoneUri);
                //intent.putExtra(AlarmClock.EXTRA_VIBRATE, HomeFragment.IsVibrateON);
            }
            else
            {
                intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
                intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_TIME);
            }

            intent.putExtra(AlarmClock.EXTRA_HOUR, Math.floor(startTimeInt / 3600));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, startTimeInt%3600 / 60);
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

            //startActivity(intent);
            intent.setPackage("com.abc.alarmclockplus.ui.alarm");
            this.getContext().startService(intent);

            return;
        }

        List<Integer> TimeListWithoutDuplicates = new ArrayList<>(new HashSet<>(TimeList));

        if(TimeList.size() != TimeListWithoutDuplicates.size())
            Toast.makeText(this.getContext(), "Spacing was to crowded, created " +
               TimeListWithoutDuplicates.size() + " alarms instead of " + numberOfAlarms, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this.getContext(), "Created " + numberOfAlarms + " alarms", Toast.LENGTH_LONG).show();

        for(int TL : TimeListWithoutDuplicates)
        {
            TL = TimeRelatedUtils.MonitorNumberOfSecondsNotExceed24h(TL);
            Intent intent;
            if(dismissOrSet)
            {
                intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                //intent.putExtra(AlarmClock.EXTRA_RINGTONE, HomeFragment.ringtoneUri);
                //intent.putExtra(AlarmClock.EXTRA_VIBRATE, HomeFragment.IsVibrateON);
            }
            else
            {
                intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
                intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_TIME);
            }

            intent.putExtra(AlarmClock.EXTRA_HOUR, Math.floor(TL / 3600));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, TL%3600 / 60);
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

            startActivity(intent);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        binding = FragmentAlarmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Button> buttonsStartHour = new ArrayList<Button>() {
            {add(binding.buttonStartHour1); add(binding.buttonStartHour2); add(binding.buttonStartHour3);}};
        List<Button> buttonsEndHour = new ArrayList<Button>() {
            {add(binding.buttonEndHour1); add(binding.buttonEndHour2); add(binding.buttonEndHour3);}};
        List<EditText> editTextsNumberSignedOfAlarms = new ArrayList<EditText>() {
            {add(binding.editTextNumberSignedOfAlarms1); add(binding.editTextNumberSignedOfAlarms2); add(binding.editTextNumberSignedOfAlarms3);}};
        List<Switch> switchesAlarm = new ArrayList<Switch>() {
            {add(binding.switchAlarm1); add(binding.switchAlarm2); add(binding.switchAlarm3);}};

        // TimePicker //
        TimePicker timePicker = binding.timePickerSpinner;
        FrameLayout frameLayoutTimePicker = binding.frameLayoutTimePicker;
        Button buttonAcceptPickedHour = binding.buttonAcceptPickedHour;
        Button buttonCancelPickedHour = binding.buttonCancelPickedHour;
        timePicker.setIs24HourView(true);
        TimePickerView(false);

        buttonAcceptPickedHour.setOnClickListener(L -> {
            String time;
            String hour = Integer.toString(timePicker.getHour());
            if(timePicker.getHour() < 10)
                hour = "0" + hour;
            String min = Integer.toString(timePicker.getMinute());
            if(timePicker.getMinute() < 10)
                min = "0" + min;
            time = hour+":"+min;

            lastClickedButton.setText(time);
            TimePickerView(false);
        });

        buttonCancelPickedHour.setOnClickListener(L -> {
            TimePickerView(false);
        });

        for (Button buttonStartHour : buttonsStartHour)
        {
            buttonStartHour.setOnClickListener(L -> {
                lastClickedButton = buttonStartHour;
                TimePickerView(true);
            });
        }

        for (Button buttonEndHour : buttonsEndHour)
        {
            buttonEndHour.setOnClickListener(L -> {
                lastClickedButton = buttonEndHour;
                TimePickerView(true);
            });
        }

        for (int i = 0; i<switchesAlarm.size(); i++)
        {
            int finalI = i;
            switchesAlarm.get(i).setOnClickListener(L -> {
                AlarmSetter(String.valueOf(buttonsStartHour.get(finalI).getText()),
                   String.valueOf(buttonsEndHour.get(finalI).getText()),
                   Integer.parseInt(String.valueOf(editTextsNumberSignedOfAlarms.get(finalI).getText())),
                   switchesAlarm.get(finalI).isChecked());
            });
        }

        alarmViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                //textView.setText(s);
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