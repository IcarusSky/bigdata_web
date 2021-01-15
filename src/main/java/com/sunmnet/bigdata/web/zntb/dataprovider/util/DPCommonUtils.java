package com.sunmnet.bigdata.web.zntb.dataprovider.util;

import com.sunmnet.bigdata.web.zntb.dataprovider.config.AggConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.DimensionConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.AggregateResult;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.ColumnIndex;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.sunmnet.bigdata.web.zntb.dataprovider.DataProvider.NULL_STRING;

public class DPCommonUtils {

    public static AggregateResult transform2AggResult(AggConfig config, List<String[]> list) throws Exception {
        // recreate a dimension stream
        Stream<DimensionConfig> dimStream = Stream.concat(config.getColumns().stream(), config.getRows().stream());
        List<ColumnIndex> dimensionList = dimStream.map(ColumnIndex::fromDimensionConfig).collect(Collectors.toList());
        int dimSize = dimensionList.size();
        dimensionList.addAll(config.getValues().stream().map(ColumnIndex::fromValueConfig).collect(Collectors.toList()));
        IntStream.range(0, dimensionList.size()).forEach(j -> dimensionList.get(j).setIndex(j));
        list.forEach(row -> {
            IntStream.range(0, dimSize).forEach(i -> {
                if (row[i] == null) row[i] = NULL_STRING;
            });
        });
        String[][] result = list.toArray(new String[][]{});
        return new AggregateResult(dimensionList, result);
    }
}
