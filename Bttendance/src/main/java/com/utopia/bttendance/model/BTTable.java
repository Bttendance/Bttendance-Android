package com.utopia.bttendance.model;

import android.util.SparseArray;

import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.model.json.PostJson;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class BTTable {

    // key: course_id, value: course
    public static SparseArray<CourseJson> CourseTable = new SparseArray<CourseJson>();

    // key: post_id, value: post
    public static SparseArray<PostJson> PostTable = new SparseArray<PostJson>();
}
