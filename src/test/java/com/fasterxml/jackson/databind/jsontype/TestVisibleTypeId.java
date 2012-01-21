package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.*;

/**
 * Tests to verify [JACKSON-437]
 */
public class TestVisibleTypeId extends BaseMapTest
{
    // type id as property, exposed
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY,
            property="type", visible=true)
    @JsonTypeName("BaseType")
    static class PropertyBean {
        public int a = 3;

        protected String type;

        public void setType(String t) { type = t; }
    }

    // as wrapper-array
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_ARRAY,
            property="type", visible=true)
    @JsonTypeName("ArrayType")
    static class WrapperArrayBean {
        public int a = 1;

        protected String type;

        public void setType(String t) { type = t; }
    }

    // as wrapper-object
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT,
            property="type", visible=true)
    @JsonTypeName("ObjectType")
    static class WrapperObjectBean {
        public int a = 2;

        protected String type;

        public void setType(String t) { type = t; }
    }

    // as external id, bit trickier
    static class ExternalIdWrapper {
        @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property="type", visible=true)
        public ExternalIdBean bean = new ExternalIdBean();
    }
    
    @JsonTypeName("ExternalType")
    static class ExternalIdBean {
        public int a = 2;

        protected String type;

        public void setType(String t) { type = t; }
    }
    
    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper mapper = new ObjectMapper();
    
    public void testVisibleWithProperty() throws Exception
    {
        String json = mapper.writeValueAsString(new PropertyBean());
        // just default behavior:
        assertEquals("{\"type\":\"BaseType\",\"a\":3}", json);
        // but then expect to read it back
        PropertyBean result = mapper.readValue(json, PropertyBean.class);
        assertEquals("BaseType", result.type);

        // also, should work with order reversed
        result = mapper.readValue("{\"a\":7, \"type\":\"BaseType\"}", PropertyBean.class);
        assertEquals(7, result.a);
        assertEquals("BaseType", result.type);
    }

    public void testVisibleWithWrapperArray() throws Exception
    {
        String json = mapper.writeValueAsString(new WrapperArrayBean());
        // just default behavior:
        assertEquals("[\"ArrayType\",{\"a\":1}]", json);
        // but then expect to read it back
        WrapperArrayBean result = mapper.readValue(json, WrapperArrayBean.class);
        assertEquals("ArrayType", result.type);
        assertEquals(1, result.a);
    }

    public void testVisibleWithWrapperObject() throws Exception
    {
        String json = mapper.writeValueAsString(new WrapperObjectBean());
        assertEquals("{\"ObjectType\":{\"a\":2}}", json);
        // but then expect to read it back
        WrapperObjectBean result = mapper.readValue(json, WrapperObjectBean.class);
        assertEquals("ObjectType", result.type);
    }

    public void testVisibleWithExternalId() throws Exception
    {
        String json = mapper.writeValueAsString(new ExternalIdWrapper());
        // but then expect to read it back
        ExternalIdWrapper result = mapper.readValue(json, ExternalIdWrapper.class);
        assertEquals("ExternalType", result.bean.type);
        assertEquals(2, result.bean.a);
    }
}