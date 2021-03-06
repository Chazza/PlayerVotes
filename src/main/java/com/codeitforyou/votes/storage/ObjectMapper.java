package com.codeitforyou.votes.storage;

import com.codeitforyou.votes.Votes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObjectMapper<T> {
    private Votes plugin = Votes.getPlugin();
    public Class clazz;

    public ObjectMapper(Class clazz) {
        this.clazz = clazz;
    }

    public T map(ResultSet rs) {
        try {
            while (rs.next()) {
                T dto = (T) clazz.getConstructor().newInstance();
                Field[] fields = dto.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if(Modifier.isPrivate(field.getModifiers())) {
                        field.setAccessible(true);
                    }

                    Column col = field.getAnnotation(Column.class);
                    if (col != null) {
                        String name = col.key();
                        try {
                            Object value = rs.getObject(name);
                            field.set(dto, value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                return dto;
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | SQLException | InvocationTargetException ex) {
            plugin.getLogger().warning("An error occurred when mapping VoteUser..");
            plugin.getLogger().warning("Reason: " + ex.getLocalizedMessage());
        }

        return null;
    }

    public List<T> mapAll(ResultSet rs) {
        List<T> objects = new ArrayList<>();
        try {

            while (rs.next()) {
                T dto = (T) clazz.getConstructor().newInstance();
                Field[] fields = dto.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if(Modifier.isPrivate(field.getModifiers())) {
                        field.setAccessible(true);
                    }

                    Column col = field.getAnnotation(Column.class);
                    if (col != null) {
                        String name = col.key();
                        try {
                            Object value = rs.getObject(name);
                            field.set(dto, value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                objects.add(dto);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | SQLException | InvocationTargetException ex) {
            plugin.getLogger().warning("An error occurred when mapping VoteUser..");
            plugin.getLogger().warning("Reason: " + ex.getLocalizedMessage());
        }

        return objects;
    }
}
