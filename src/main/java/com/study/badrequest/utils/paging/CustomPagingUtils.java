package com.study.badrequest.utils.paging;

public class CustomPagingUtils {

    private int setLimitSize(Integer defaultSize,Integer limitSize) {

        final int defaultLimitSize = 10;

        return limitSize == null ? defaultLimitSize : limitSize;
    }

    private int setLimitSize(int defaultSize,Integer limitSize) {

        return limitSize == null ? defaultSize : limitSize;
    }

}
