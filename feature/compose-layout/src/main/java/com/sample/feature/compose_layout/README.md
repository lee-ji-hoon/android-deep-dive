<!-- TOC -->
* [Android Layout에 대해서 Deep-Dive 해보자.](#android-layout에-대해서-deep-dive-해보자)
  * [Layout Model](#layout-model)
    * [그럼 실제 코드를 예시로 Measure Place를 보자](#그럼-실제-코드를-예시로-measure-place를-보자)
    * [간단 정리](#간단-정리)
<!-- TOC -->

# Android Layout에 대해서 Deep-Dive 해보자.

## 🆕 Layout Model

> 우선 Compose는 State를 UI로 변환을 한다.

`Composition > Layout > Drawing` 이 순서로 UI가 그려진다는 것은 많이들 봤을 것이다.

위 3가지 단계 중 내가 유심히 볼 것은 Layout 단계이다.

![image](https://github.com/lee-ji-hoon/android-playground/assets/53300830/cdb1904a-5ec2-48aa-9d23-922cf3c46758)

- Layout 은 Measure(측정) 과 Place(배치)로 나뉜다.
  - 이해하기 쉽게 하기 위해서 Measure, Place로 나뉜다고 했지만 실제 Layout을 직접 사용해보면 알겠지만 하나의 단계이다.
- View가 익숙한 사람들은 `onMeasure` / `onLayout` 으로 이해하면 된다.

### 🤔 그럼 실제 코드를 예시로 Measure Place를 보자

```kotlin
@Composable
fun LayoutSampleScreen() {
    Row {
        Image(/** ... **/)
        Column {
            Text(text = "first")
            Text(text = "second")
        }
    }
}
```

위와 같은 경우 어떻게 Measure되고 Place를 될지 생각을 해보자.

우선 간단히 생각을 해보면 `Row`가 Measure가 되고, Place가 된 후에 Image ... Text 순서대로 될 것 같지만 실제로는 아래와 같다. 

![image](https://github.com/lee-ji-hoon/android-playground/assets/53300830/32563dde-2a78-48f1-a5c6-ee4acf4624c4)

> Leaf Node > 자식이 없는 노드

1. Row가 `Measure` 된다. 자식이 있기에 아직 `Place`되지 않는다.
2. Image가 `Measure` 된다. Leaf Nod이기에 바로 Size 측정 후 `Place` 된다.
3. Column이 `Measure` 된다. 자식이 있기에 아직 `Place`되지 않는다.
4. Text가 `Measure` 된다. Leaf Nod이기에 바로 Size 측정 후 `Place` 된다.
5. Text가 `Measure` 된다. Leaf Nod이기에 바로 Size 측정 후 `Place` 된다.
6. Colum의 자식들이 전부 `Place` 됐기에 본인도 `Place` 된다.
7. Row의 자식들이 전부 `Place` 됐기에 본인도 `Place` 된다.

### 📚간단 정리

즉 **Leaf Node** 라면 바로 Measure -> Place가 되는 것이고, 자식이 있다면 Measure > 자식들 Place > 본인 Place 형태로 되는 것이다.



