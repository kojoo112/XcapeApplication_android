package com.samsan.xcapeapplication.retrofit;

import com.samsan.xcapeapplication.vo.HintVO;
import com.samsan.xcapeapplication.vo.MerchantVO;
import com.samsan.xcapeapplication.vo.ThemeVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("api/merchant/list")
    Call<List<MerchantVO>> getMerchantList();

    @GET("api/theme/list")
    Call<List<ThemeVO>> getThemeList(@Query("merchantCode") String merchantCode);

    @GET("api/getHintList")
    Call<List<HintVO>> getHintList(@Query("merchant") String merchant, @Query("themeCode") String themeCode);
}
