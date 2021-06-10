package cn.enaium.accessor;

import cn.enaium.accessor.annotaiton.Accessor;
import cn.enaium.accessor.annotaiton.Field;
import cn.enaium.accessor.annotaiton.Method;

/**
 * @author Enaium
 */
@Accessor(Test.class)
public interface ITest {
    @Field("name")
    String getName();

    @Field("name")
    void setName(String name);


    @Field("id")
    void setID(String id);

    @Field("id")
    String getID();

    @Method("render")
    void InvokeRender(String arg);

    @Method("render")
    void InvokeRender(String[] strings);
}
