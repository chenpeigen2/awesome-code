> xargs
> screen
> grep
>

### 一些例子
```shell
find packages/ -name 'Android.bp' | xargs -i grep -HE --color '\s?bootstrap_go_package\s?' {}
cat telephony-product.mk | grep -E "^QTI_TELEPHONY_FWK.?"
ls *.txt | xargs -I {} cp {} /home/mi/Desktop
```