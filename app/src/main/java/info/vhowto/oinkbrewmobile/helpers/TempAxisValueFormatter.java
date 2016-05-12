package info.vhowto.oinkbrewmobile.helpers;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

public class TempAxisValueFormatter implements YAxisValueFormatter {

    private DecimalFormat mFormat;

    public TempAxisValueFormatter () {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value) + " C";
    }
}
