package dulleh.akhyou.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransform implements Transformation{
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        try {
            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(14.f);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }

    @Override
    public String key() {
        return "dulleh()";
    }

}
