package dulleh.akhyou.Utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

public class PaletteTransform implements Transformation{
    private Palette pallete;

    @Override
    public Bitmap transform(Bitmap source) {

        pallete = Palette.generate(source);

        return source;
    }

    @Override
    public String key() {
        return "dulleh()";
}

    public Palette getPallete() {
        return pallete;
    }
}
