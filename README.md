# accessor

Invoke private field and method

## Usage

```java
@Accessor("cn.enaium.accessor.Test")
public interface ITest {
    @Field("name")
    String getName();

    @Field("name")
    void setName(String name);

    @Method("render")
    void InvokeRender(String arg);
}
```

Target class

```java
public class Test {
    private String name = "Enaium";

    private void render(String arg) {
        System.out.println(arg);
    }
}
```

### Configuration

```json
{
  "accessors": [
    "cn.enaium.accessor.ITest"
  ]
}
```

```java
public static void main(String[] args) {

    //Add configuration
    Accessor.addConfiguration(Main.class, "accessor.config.json");

    //Transform
    Accessor.transform(basic);
    
    Test test = new Test();
    ((ITest) test).setName("Name");
    System.out.println(((ITest) test).getName());
    ((ITest) test).InvokeRender("render");
}
```

### Remapping

```java
@Accessor("Test")
public interface ITest {
    @Field("rename")
    String getName();

    @Field("rename")
    void setName(String name);

    @Method("display")
    void InvokeRender(String arg);
}
```

```json
{
  "Test": "cn.enaium.accessor.Test",
  "rename": "name",
  "display": "render"
}
```

```json
{
  "accessors": [
    "cn.enaium.accessor.ITest"
  ],
  "remapping": "accessor.remapping.json"
}
```