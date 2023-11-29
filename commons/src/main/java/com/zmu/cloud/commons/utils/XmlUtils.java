package com.zmu.cloud.commons.utils;

import java.io.StringReader;
import java.lang.reflect.Field;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

@Slf4j
public class XmlUtils {


    /**
     * 将xml转换成java bean,支持  <![CDATA[]]>,java bean必须要有默认的构造方法
     */
    public static final <T> T parse(String xml, Class<T> clazz) throws Exception {
        return reflectiveEntity(clazz, loadDocument(xml).getDocumentElement());
    }

    private static final Document loadDocument(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(xml);
        InputSource is = new InputSource(sr);
        return db.parse(is);
    }


    /**
     * 通过反射将xml值设置到java bean
     */
    private static <T> T reflectiveEntity(Class<T> clazz, Element root)
            throws InstantiationException, IllegalAccessException {
        T entity = clazz.newInstance();
        Field[] fs = clazz.getDeclaredFields();
        for (Field field : fs) {
            field.setAccessible(true);
            String name = field.getName();
            Node node = root.getElementsByTagName(name).item(0);
            if (node != null) {
                field.set(entity, node.getTextContent());
            }
        }
        return entity;
    }
}
