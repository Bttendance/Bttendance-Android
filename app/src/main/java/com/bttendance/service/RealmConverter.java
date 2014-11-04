package com.bttendance.service;

import java.lang.reflect.Type;

import io.realm.RealmObject;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by TheFinestArtist on 14. 10. 21..
 */
/**
 * A {@link Converter} which uses Realm for reading and writing entities.
 *
 * @author TheFinestArtist (contact@thefinestartist.com)
 */
public class RealmConverter implements Converter {
    private static final String MIME_TYPE = "application/json; charset=UTF-8";

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        return null;
    }

    @Override
    public TypedOutput toBody(Object object) {
        return null;
    }
}
