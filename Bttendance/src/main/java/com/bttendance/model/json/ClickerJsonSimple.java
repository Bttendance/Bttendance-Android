package com.bttendance.model.json;

import android.content.Context;

import com.bttendance.R;

import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class ClickerJsonSimple extends BTJsonSimple {

    public int choice_count;
    public int[] a_students;
    public int[] b_students;
    public int[] c_students;
    public int[] d_students;
    public int[] e_students;
    public int post;

    public String getDetail() {
        int total = a_students.length + b_students.length + c_students.length + d_students.length + e_students.length;
        int a = 0, b = 0, c = 0, d = 0, e = 0;
        if (total != 0) {
            b = Math.round((float) b_students.length * 100.0f / (float) total);
            c = Math.round((float) c_students.length * 100.0f / (float) total);
            d = Math.round((float) d_students.length * 100.0f / (float) total);
            e = Math.round((float) e_students.length * 100.0f / (float) total);
            a = 100 - b - c - d - e;
        }

        String message = "A : " + a + "%";
        message += "  B : " + b + "%";
        if (choice_count > 2)
            message += "  C : " + c + "%";
        if (choice_count > 3)
            message += "  D : " + d + "%";
        if (choice_count > 4)
            message += "  E : " + e + "%";
        return message;
    }

    public CategorySeries getSeries() {
        CategorySeries series = new CategorySeries("");

        int total = a_students.length + b_students.length + c_students.length + d_students.length + e_students.length;

        if (total == 0)
            series.add("A", 1);
        else
            series.add("A", a_students.length);

        if (total == 0)
            series.add("B", 1);
        else
            series.add("B", b_students.length);

        if (choice_count > 2) {
            if (total == 0)
                series.add("C", 1);
            else
                series.add("C", c_students.length);
        }

        if (choice_count > 3) {
            if (total == 0)
                series.add("D", 1);
            else
                series.add("D", d_students.length);
        }

        if (choice_count > 4) {
            if (total == 0)
                series.add("E", 1);
            else
                series.add("E", e_students.length);
        }
        return series;
    }

    public DefaultRenderer getRenderer(Context context) {
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(context.getResources().getColor(R.color.bttendance_white));
        renderer.setZoomButtonsVisible(false);
        renderer.setClickEnabled(false);
        renderer.setZoomEnabled(false);
        renderer.setShowLabels(false);
        renderer.setPanEnabled(false);
        renderer.setShowLegend(false);
        renderer.setAntialiasing(true);
        renderer.setMargins(new int[]{0, 0, 0, 0});
        renderer.setStartAngle(-90);

        // A renderer
        {
            SimpleSeriesRenderer render = new SimpleSeriesRenderer();
            render.setColor(context.getResources().getColor(R.color.bttendance_a));
            renderer.addSeriesRenderer(render);
        }
        // B renderer
        {
            SimpleSeriesRenderer render = new SimpleSeriesRenderer();
            render.setColor(context.getResources().getColor(R.color.bttendance_b));
            renderer.addSeriesRenderer(render);
        }
        // C renderer
        if (choice_count > 2) {
            SimpleSeriesRenderer render = new SimpleSeriesRenderer();
            render.setColor(context.getResources().getColor(R.color.bttendance_c));
            renderer.addSeriesRenderer(render);
        }
        // D renderer
        if (choice_count > 3) {
            SimpleSeriesRenderer render = new SimpleSeriesRenderer();
            render.setColor(context.getResources().getColor(R.color.bttendance_d));
            renderer.addSeriesRenderer(render);
        }
        // E renderer
        if (choice_count > 4) {
            SimpleSeriesRenderer render = new SimpleSeriesRenderer();
            render.setColor(context.getResources().getColor(R.color.bttendance_e));
            renderer.addSeriesRenderer(render);
        }

        return renderer;
    }
}
