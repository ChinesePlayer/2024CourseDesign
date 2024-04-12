# 2024CourseDesign
2023级2024年大一下Java高级程序设计课程设计的代码仓库

# 引

通过这几天的捣鼓，以一个页面 (菜单管理页面) 的重写为实践经验，大概明白了这个框架的工作原理，以及如何使用。

# 前端

## 交互与UI界面

### FXML文件

JavaFX的交互与UI界面是可以分开写的，即将UI界面放在FXML文件中(该文件可以通过SceneBuilder这个UI可视化工具来快速制作)，而将交互相关的实现放在java代码文件中，这样说可能有些抽象，直接看一个例子就明白了。

在每一个FXML文件中，有且仅有一个根节点，该根节点中可以指定一些属性，例如项目中的`student-panel.fxml`：

```xml
...
<BorderPane prefWidth="1053.0" 
            xmlns="http://javafx.com/javafx/17.0.2-ea" 
            xmlns:fx="http://javafx.com/fxml/1" 
            fx:controller="com.teach.javafx.controller.StudentController">
...
```

比较好理解的是与UI元素相关的属性，例如`preWidth`，表示该组件的首选高度(在没有其他限制时采取的高度)。

除此之外其中还有与fxml协议有关的属性，例如`xmlns`和`xmlns:fx`，这定义了我们可以使用哪些标签，以及如何解析这些标签，例如此处出现的`<BorderPane>`标签。这两个属性看起来像网站，但其实不是。

还有与交互有关的标签，如此处的`fx:controller`，可以看到这个标签的值是一大坨，这一大坨的最后一部分其实是一个类，那么就明了了，这个类名字之前的那一大坨其实是这个类所在的包，即文件夹(**这个概念很重要，在Java中经常使用，被称为类的全称**)。这个类其实就是前文所说的控制交互的代码。

所以如果我们要编写一个页面，这个页面的UI应该放在FXML文件中，而这个页面的交互应该放在一个Java类中，并且要在FXML文件中指定使用这个类作为控制类(即使用`fx:controller`属性)来将它们绑定在一起。

### Java交互类

所谓交互，无非就是接受用户的输入，并给用户反馈，这里的用户输入是广义的，不仅指键盘输入，还有鼠标点击，用户事件等等。

绑定完Java代码和FXML文件后，我们可以通过Java注解+FXML设置`fx:id`属性的方式来获取UI中的某个控件并且获取、操纵其中的数据。例如有这样一个文本输入框: 

```xml
<TextField fx:id="numField" prefHeight="37.0" prefWidth="230.0"/>
```

我们通过`fx:id`指定了这个UI控件对应Java代码中的哪个变量，`fx:id`的值必须与Java代码中的变量名相同，以下是Java代码:

```java
public class ExampleController{
    @FXML
    private TextField numField;
    
    @FXML
    public void initialize(){
        numField.setText("Hello World");
    }
}
```

这里出现了一个新的东西: `initialize()`方法，这个方法被`@FXML`注解后，JavaFX会自动在页面第一次加载时调用，注意一定要用@FXML注解，否则不会被自动调用。在这个方法中，我们可以在其中进行一些初始化操作，在此处，仅仅是将文本输入框的内容设为了`Hello World`

### 小结

上面就是使用JavaFX进行编程的基本流程，在JavaFX中，每种UI组件都有许多方法可以供我们使用，只要是你在FXML文件中能指定的属性，都能通过Java代码来修改。更多的UI组件这里不能一一列举，需要时可以查阅官网文档或者询问ChatGPT。
