import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_rust_demo/src/utils/unit_converter.dart';

void main() {
  group('UnitConverter Tests', () {
    
    group('温度换算', () {
      test('摄氏度转华氏度', () {
        final result = UnitConverter.convert('温度', '摄氏度 (°C)', '华氏度 (°F)', '0');
        expect(result, equals('32'));
        
        final result2 = UnitConverter.convert('温度', '摄氏度 (°C)', '华氏度 (°F)', '100');
        expect(result2, equals('212'));
      });
      
      test('摄氏度转开尔文', () {
        final result = UnitConverter.convert('温度', '摄氏度 (°C)', '开尔文 (K)', '0');
        expect(result, equals('273.15'));
        
        final result2 = UnitConverter.convert('温度', '摄氏度 (°C)', '开尔文 (K)', '27');
        expect(result2, equals('300.15'));
      });
      
      test('华氏度转摄氏度', () {
        final result = UnitConverter.convert('温度', '华氏度 (°F)', '摄氏度 (°C)', '32');
        expect(result, equals('0'));
        
        final result2 = UnitConverter.convert('温度', '华氏度 (°F)', '摄氏度 (°C)', '212');
        expect(result2, equals('100'));
      });
      
      test('开尔文转摄氏度', () {
        final result = UnitConverter.convert('温度', '开尔文 (K)', '摄氏度 (°C)', '273.15');
        expect(result, equals('0'));
        
        final result2 = UnitConverter.convert('温度', '开尔文 (K)', '摄氏度 (°C)', '373.15');
        expect(result2, equals('100'));
      });
    });

    group('长度换算', () {
      test('千米转米', () {
        final result = UnitConverter.convert('长度', '千米 (km)', '米 (m)', '1');
        expect(result, equals('1000'));
      });
      
      test('米转厘米', () {
        final result = UnitConverter.convert('长度', '米 (m)', '厘米 (cm)', '1');
        expect(result, equals('100'));
      });
      
      test('英寸转厘米', () {
        final result = UnitConverter.convert('长度', '英寸 (in)', '厘米 (cm)', '1');
        expect(result, equals('2.54'));
      });
      
      test('英里转千米', () {
        final result = UnitConverter.convert('长度', '英里 (mi)', '千米 (km)', '1');
        expect(double.parse(result!), closeTo(1.609, 0.001));
      });
    });

    group('速度换算', () {
      test('千米/时转米/秒', () {
        final result = UnitConverter.convert('速度', '千米/时 (km/h)', '米/秒 (m/s)', '36');
        expect(result, equals('10'));
      });
      
      test('马赫转米/秒', () {
        final result = UnitConverter.convert('速度', '马赫 (Ma)', '米/秒 (m/s)', '1');
        expect(double.parse(result!), closeTo(340.29, 0.01));
      });
    });

    group('时间换算', () {
      test('小时转分钟', () {
        final result = UnitConverter.convert('时间', '时 (h)', '分 (min)', '1');
        expect(result, equals('60'));
      });
      
      test('天转小时', () {
        final result = UnitConverter.convert('时间', '天 (d)', '时 (h)', '1');
        expect(result, equals('24'));
      });
      
      test('周转天', () {
        final result = UnitConverter.convert('时间', '周 (wk)', '天 (d)', '1');
        expect(result, equals('7'));
      });
    });

    group('质量换算', () {
      test('千克转克', () {
        final result = UnitConverter.convert('质量', '千克 (kg)', '克 (g)', '1');
        expect(result, equals('1000'));
      });
      
      test('磅转千克', () {
        final result = UnitConverter.convert('质量', '磅 (lb)', '千克 (kg)', '1');
        expect(double.parse(result!), closeTo(0.454, 0.001));
      });
    });

    group('面积换算', () {
      test('平方千米转平方米', () {
        final result = UnitConverter.convert('面积', '平方千米 (km²)', '平方米 (m²)', '1');
        expect(result, equals('1000000'));
      });
      
      test('公顷转平方米', () {
        final result = UnitConverter.convert('面积', '公顷 (ha)', '平方米 (m²)', '1');
        expect(result, equals('10000'));
      });
    });

    group('体积换算', () {
      test('升转毫升', () {
        final result = UnitConverter.convert('体积', '升 (L)', '毫升 (mL)', '1');
        expect(result, equals('1000'));
      });
      
      test('立方米转升', () {
        final result = UnitConverter.convert('体积', '立方米 (m³)', '升 (L)', '1');
        expect(result, equals('1000'));
      });
    });

    group('压强换算', () {
      test('标准大气压转帕斯卡', () {
        final result = UnitConverter.convert('压强', '标准大气压 (atm)', '帕斯卡 (Pa)', '1');
        expect(double.parse(result!), closeTo(101325, 1));
      });
      
      test('巴转帕斯卡', () {
        final result = UnitConverter.convert('压强', '巴 (bar)', '帕斯卡 (Pa)', '1');
        expect(result, equals('100000'));
      });
    });

    group('电压换算', () {
      test('千伏转伏特', () {
        final result = UnitConverter.convert('电压', '千伏 (kV)', '伏特 (V)', '1');
        expect(result, equals('1000'));
      });
      
      test('伏特转毫伏', () {
        final result = UnitConverter.convert('电压', '伏特 (V)', '毫伏 (mV)', '1');
        expect(result, equals('1000'));
      });
    });

    group('进制换算', () {
      test('十进制转二进制', () {
        final result = UnitConverter.convert('进制', '十进制', '二进制', '10');
        expect(result, equals('1010'));
      });
      
      test('十进制转八进制', () {
        final result = UnitConverter.convert('进制', '十进制', '八进制', '10');
        expect(result, equals('12'));
      });
      
      test('十进制转十六进制', () {
        final result = UnitConverter.convert('进制', '十进制', '十六进制', '255');
        expect(result, equals('FF'));
      });
      
      test('二进制转十进制', () {
        final result = UnitConverter.convert('进制', '二进制', '十进制', '1010');
        expect(result, equals('10'));
      });
      
      test('十六进制转十进制', () {
        final result = UnitConverter.convert('进制', '十六进制', '十进制', 'FF');
        expect(result, equals('255'));
      });
      
      test('二进制转十六进制', () {
        final result = UnitConverter.convert('进制', '二进制', '十六进制', '11111111');
        expect(result, equals('FF'));
      });
    });

    group('边界情况测试', () {
      test('空输入返回null', () {
        final result = UnitConverter.convert('温度', '摄氏度 (°C)', '华氏度 (°F)', '');
        expect(result, isNull);
      });
      
      test('无效数字输入返回null', () {
        final result = UnitConverter.convert('温度', '摄氏度 (°C)', '华氏度 (°F)', 'abc');
        expect(result, isNull);
      });
      
      test('负数温度换算', () {
        final result = UnitConverter.convert('温度', '摄氏度 (°C)', '华氏度 (°F)', '-40');
        expect(result, equals('-40'));
      });
      
      test('小数输入', () {
        final result = UnitConverter.convert('长度', '米 (m)', '厘米 (cm)', '1.5');
        expect(result, equals('150'));
      });
    });
  });
}
