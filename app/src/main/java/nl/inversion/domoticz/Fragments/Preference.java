package nl.inversion.domoticz.Fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import java.util.Set;

import nl.inversion.domoticz.R;
import nl.inversion.domoticz.Utils.PhoneConnectionUtil;
import nl.inversion.domoticz.Utils.SharedPrefUtil;

public class Preference extends PreferenceFragment {

    SharedPrefUtil mSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        mSharedPrefs = new SharedPrefUtil(getActivity());

        setStartUpScreenDefaultValue();
        setLocalServerSsid();

    }

    private void setLocalServerSsid() {
        Set<String> ssids = mSharedPrefs.getLocalSsid();

        PhoneConnectionUtil mPhoneConnectionUtil = new PhoneConnectionUtil(getActivity());
        MultiSelectListPreference localServerSsid =
                (MultiSelectListPreference) findPreference("local_server_ssid");
        // Setting summary at runtime because setting it in XML crashes the app in API 15,
        // in API 22 is all good
        localServerSsid.setSummary(R.string.local_server_local_wifi_ssid_list_summary);

        CharSequence[] ssidEntries = mPhoneConnectionUtil.startSsidScanAsCharSequence();

        if (ssidEntries.length < 1) {
            ssidEntries = new CharSequence[1];
            ssidEntries[0] = getString(R.string.msg_no_ssid_found); // no wifi ssid nearby found!
        }

        localServerSsid.setEntries(ssidEntries);
        localServerSsid.setEntryValues(ssidEntries);
    }

    private void setStartUpScreenDefaultValue() {

        int defaultValue = mSharedPrefs.getStartupScreenIndexValue();

        ListPreference startup_screen = (ListPreference) findPreference("startup_screen");
        startup_screen.setValueIndex(defaultValue);

    }

    @Override
    public void onPause() {
        super.onPause();

        SwitchPreference useSameAddress =
                (SwitchPreference) findPreference("local_server_uses_different_address");
        if (!useSameAddress.isChecked()) mSharedPrefs.setLocalSameAddressAsRemote();
    }
}