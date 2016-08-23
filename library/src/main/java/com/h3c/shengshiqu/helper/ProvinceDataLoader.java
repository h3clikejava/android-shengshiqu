package com.h3c.shengshiqu.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.h3c.shengshiqu.models.CityModel;
import com.h3c.shengshiqu.models.DistrictModel;
import com.h3c.shengshiqu.models.ProvinceModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by H3c on 16/8/23.
 */

public class ProvinceDataLoader {
    private final static int DATA_LOAD_STATE_DEFAULT = 0;// 初始化
    private final static int DATA_LOAD_STATE_LOADING = 1;// 加载中
    private final static int DATA_LOAD_STATE_DONE = 2;// 加载完成
    private final static int DATA_LOAD_STATE_ERROR = 3;// 加载错误

    private static ProvinceDataLoader instance;

    private int errorRetryTimes = 3;// 失败重试次数
    private int dataLoadState = DATA_LOAD_STATE_DEFAULT;
    public static ProvinceDataLoader getInstance() {
        if(instance == null) {
            instance = new ProvinceDataLoader();
        }

        return instance;
    }

    private ProvinceDataLoader() {}

    public interface ProvinceDataLoaderListener {
        void provinceDataLoaderDone(String[] provinceData, Map<String, String[]> citiesData, Map<String, String[]> districtData);
        void provinceDataLoaderError();
    }

    public synchronized void getData(Context context, final ProvinceDataLoaderListener listener) {
        switch (dataLoadState) {
            case DATA_LOAD_STATE_DEFAULT: {
                dataLoadState = DATA_LOAD_STATE_LOADING;

                new AsyncTask<Context, Void, Void>() {
                    @Override
                    protected Void doInBackground(Context... c) {
                        initProvinceDatas(c[0]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if(dataLoadState == DATA_LOAD_STATE_DONE) {
                            if(listener != null) {
                                listener.provinceDataLoaderDone(mProvinceDatas, mCitisDatasMap, mDistrictDatasMap);
                            }
                        } else {
                            if(listener != null) {
                                listener.provinceDataLoaderError();
                            }
                        }
                    }
                }.execute(context);
                break;
            }
            case DATA_LOAD_STATE_LOADING: {
                // Loading 先做错误简单处理
                if(listener != null) {
                    listener.provinceDataLoaderError();
                }
                break;
            }
            case DATA_LOAD_STATE_DONE: {
                if(listener != null) {
                    listener.provinceDataLoaderDone(mProvinceDatas, mCitisDatasMap, mDistrictDatasMap);
                }
                break;
            }
            case DATA_LOAD_STATE_ERROR: {
                if(listener != null) {
                    listener.provinceDataLoaderError();
                }

                // 重新尝试
                if(errorRetryTimes < 0) return;
                errorRetryTimes--;
                dataLoadState = DATA_LOAD_STATE_DEFAULT;
                getData(context, listener);
                break;
            }
        }
    }

    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName ="";

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode ="";

    /**
     * 解析省市区的XML数据, 后台处理
     */
    protected void initProvinceDatas(Context context) {
        if(context == null) return;
        List<ProvinceModel> provinceList;
        AssetManager asset = context.getAssets();
        InputStream input = null;
        try {
            input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);

            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList!= null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList!= null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }

            mProvinceDatas = new String[provinceList.size()];
            for (int i=0; i< provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j=0; j< cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k=0; k<districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }

            dataLoadState = DATA_LOAD_STATE_DONE;
        } catch (Throwable e) {
            e.printStackTrace();
            dataLoadState = DATA_LOAD_STATE_ERROR;
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
