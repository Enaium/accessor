package cn.enaium.accessor;

import cn.enaium.accessor.annotaiton.Accessor;
import cn.enaium.accessor.annotaiton.Field;
import cn.enaium.accessor.annotaiton.Method;

/**
 * @author Enaium
 */
@Accessor("Test")
public interface ITest {
    @Field("rename")
    String getName();

    @Field("rename")
    void setName(String name);

    @Method("display")
    void InvokeRender(String arg);
}
