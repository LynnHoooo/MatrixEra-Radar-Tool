/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jstyle.test2208.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.jstyle.blesdk2208.model.ExtendedBluetoothDevice;
import com.jstyle.test2208.R;
import com.jstyle.test2208.Util.PermissionsUtil;
import com.jstyle.test2208.Util.ResolveData;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends AppCompatActivity implements PermissionsUtil.PermissionListener {
    @BindView(R.id.list_view)
    ListView listView;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }


    public static String assetsPath = "J1668_06_V011_3_20180326.zip";

    //public static String assetsPath = "J1638_10_ONE_V015_4_20180309.zip";
    private void copyFileFromAssets(String filePath) {
        File file = new File(getFilesDir().getAbsolutePath());
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            File fileOTA = new File(file, "ota");
            if (!fileOTA.exists()) {
                fileOTA.mkdir();
            }
            File fileMusic = new File(fileOTA, assetsPath);
            if (fileMusic.exists()) {
                return;
            } else {
                fileMusic.createNewFile();
            }
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(assetsPath);
            OutputStream outputStream = new FileOutputStream(fileMusic);
            byte[] buffer = new byte[1024];
            while (inputStream.read(buffer) > 0) {
                outputStream.write(buffer);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                PermissionsUtil.requestPermissions(this, this, Manifest.permission.ACCESS_FINE_LOCATION);

                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
            case R.id.menu_filter:
                //showFilterDialog();
                break;
        }
        return true;
    }
//    FilterDialog filterDialog;
//    private void showFilterDialog() {
//        if(filterDialog==null){
//            filterDialog=new FilterDialog(this,this);
//        }
//        if(!filterDialog.isShowing())filterDialog.show();
////
//    }

    private void changeFilter() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null)
                    return;
                String name = mLeDeviceListAdapter.getName(position);
                if (mScanning) {
                    scanLeDevice(false);
                }
                final Intent intent = new Intent(DeviceScanActivity.this, MainActivity.class);
                intent.putExtra("address", device.getAddress());
                intent.putExtra("name", name);
                startActivity(intent);

            }
        });
        setListAdapter(mLeDeviceListAdapter);
        if(Build.VERSION.SDK_INT<31&&Build.VERSION.SDK_INT>=23){
            PermissionsUtil.Permission_Scan(DeviceScanActivity.this, new PermissionsUtil.PermissionsUtilsListenerer() {
                @Override
                public void onSuccess() {
                    Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                    List<ExtendedBluetoothDevice> list = new ArrayList<>();
                    for (BluetoothDevice device : devices) {
                        list.add(new ExtendedBluetoothDevice(device));
                    }
                    mLeDeviceListAdapter.addBondDevice(list);
                    scanLeDevice(true);
                }

                @Override
                public void onfail() {

                }

                @Override
                public void onerror() {

                }
            });
        }   else if(Build.VERSION.SDK_INT>=31){
            PermissionsUtil.Permission_ScanNew(DeviceScanActivity.this, new PermissionsUtil.PermissionsUtilsListener() {
                @Override
                public void onSuccess() {
                    Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                    List<ExtendedBluetoothDevice> list = new ArrayList<>();
                    for (BluetoothDevice device : devices) {
                        list.add(new ExtendedBluetoothDevice(device));
                    }
                    mLeDeviceListAdapter.addBondDevice(list);
                    scanLeDevice(true);
                }
                @Override
                public void onfail() {

                }
            });
        }else{
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            List<ExtendedBluetoothDevice> list = new ArrayList<>();
            for (BluetoothDevice device : devices) {
                list.add(new ExtendedBluetoothDevice(device));
            }
            mLeDeviceListAdapter.addBondDevice(list);
            scanLeDevice(true);
        }

    }

    private void setListAdapter(LeDeviceListAdapter mLeDeviceListAdapter) {
        listView.setAdapter(mLeDeviceListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        extendedBluetoothDevices.clear();
        mLeDeviceListAdapter.clear();


    }


    private void scanLeDevice(final boolean enable) {
        if(Build.VERSION.SDK_INT<31&&Build.VERSION.SDK_INT>=23){
            PermissionsUtil.Permission_Scan(DeviceScanActivity.this, new PermissionsUtil.PermissionsUtilsListenerer() {
                @Override
                public void onSuccess() {
                    ss(enable);
                }

                @Override
                public void onfail() {

                }

                @Override
                public void onerror() {

                }
            });
        }   else if(Build.VERSION.SDK_INT>=31){
            PermissionsUtil.Permission_ScanNew(DeviceScanActivity.this, new PermissionsUtil.PermissionsUtilsListener() {
                @Override
                public void onSuccess() {
                    ss(enable);
                }
                @Override
                public void onfail() {

                }
            });
        }else{
            ss(enable);
        }
        //if (ss(enable)) return;
        invalidateOptionsMenu();
    }

    private boolean ss(boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                    invalidateOptionsMenu();
                }
            }, 12000);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //	mBluetoothAdapter.startLeScan(serviceUuids, mLeScanCallback);
            mScanning = true;
        } else {
            if (!mScanning) return true;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mHandler.removeCallbacksAndMessages(null);
            mScanning = false;
        }
        return false;
    }

    int filterRssi = -100;
    private List<ExtendedBluetoothDevice> extendedBluetoothDevices = new ArrayList<>();

    private ExtendedBluetoothDevice findDevice(final BluetoothDevice device) {
        for (final ExtendedBluetoothDevice mDevice : extendedBluetoothDevices) {
            if (mDevice.matches(device)) return mDevice;
        }
        return null;
    }

    public void addDevice(BluetoothDevice device, String name, int rssi) {
        ExtendedBluetoothDevice bluetoothDevice = findDevice(device);
        if (bluetoothDevice == null) {
            extendedBluetoothDevices.add(new ExtendedBluetoothDevice(device, name, rssi));
        } else {
            bluetoothDevice.rssi = rssi;
        }
    }

    @Override
    public void granted(String name) {
        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(name)) {

        } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(name)) {
            if (mLeDeviceListAdapter != null) mLeDeviceListAdapter.clear();
            if (extendedBluetoothDevices != null) extendedBluetoothDevices.clear();
            scanLeDevice(true);
        }
    }

    @Override
    public void NeverAskAgain() {

    }

    @Override
    public void disallow(String name) {

    }


    private class LeDeviceListAdapter extends BaseAdapter {
        private List<ExtendedBluetoothDevice> deviceList;
        private LayoutInflater mInflator;
        int filterRssi;

        public void setDeviceList(List<ExtendedBluetoothDevice> deviceList) {
            this.deviceList = deviceList;
            notifyDataSetChanged();
            //getFilter().filter(filterName);
        }

        public void setFilterRssi(int rssi) {
            this.filterRssi = rssi;
        }

        public LeDeviceListAdapter() {
            super();
            deviceList = new ArrayList<>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addBondDevice(List<ExtendedBluetoothDevice> list) {

            deviceList.addAll(list);
            notifyDataSetChanged();

        }

        public void addDevice(BluetoothDevice device, String name, int rssi) {
            ExtendedBluetoothDevice bluetoothDevice = findDevice(device);
            if (bluetoothDevice == null) {
                deviceList.add(new ExtendedBluetoothDevice(device, name, rssi));
            } else {
                bluetoothDevice.rssi = rssi;
            }
        }

        private ExtendedBluetoothDevice findDevice(final BluetoothDevice device) {
            for (final ExtendedBluetoothDevice mDevice : deviceList) {
                if (mDevice.matches(device)) return mDevice;
            }
            return null;
        }

        public BluetoothDevice getDevice(int position) {
            return deviceList.get(position).device;
        }

        public String getName(int position) {
            return deviceList.get(position).name;
        }

        public void clear() {
            deviceList.clear();
        }

        @Override
        public int getCount() {
            return deviceList == null ? 0 : deviceList.size();
        }

        @Override
        public Object getItem(int i) {
            return deviceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view
                        .findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                //   viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ExtendedBluetoothDevice extendedBluetoothDevice = deviceList.get(i);
            BluetoothDevice device = extendedBluetoothDevice.device;
            final String deviceName = extendedBluetoothDevice.name;
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);

            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            if (extendedBluetoothDevice.rssi == ExtendedBluetoothDevice.NO_RSSI) {
                viewHolder.deviceRssi.setText("Bonded");
            } else {
                viewHolder.deviceRssi.setText("Rssi:" + extendedBluetoothDevice.rssi);
            }

            return view;
        }


    }


    // Device scan callback.

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = device.getName();
                    if (TextUtils.isEmpty(deviceName)) {
                        deviceName = ResolveData.decodeDeviceName(scanRecord);
                    }
                    /*if(!TextUtils.isEmpty(deviceName)&&deviceName.toLowerCase().contains("2208")){*/
                        addDevice(device, deviceName, rssi);
                        //if (TextUtils.isEmpty(filterName) || deviceName.toLowerCase().contains(filterName)) {
                        if (rssi > filterRssi) {
                            mLeDeviceListAdapter.addDevice(device, deviceName, rssi);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    /*}*/

                    // }


                }
            });
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }


}