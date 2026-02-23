/// 单位换算工具类
class UnitConverter {
  // 定义各类单位
  static const Map<String, List<String>> unitCategories = {
    '温度': ['摄氏度 (°C)', '华氏度 (°F)', '开尔文 (K)'],
    '长度': ['米 (m)', '千米 (km)', '厘米 (cm)', '毫米 (mm)', '英寸 (in)', '英尺 (ft)', '英里 (mi)', '海里 (nmi)'],
    '速度': ['米/秒 (m/s)', '千米/时 (km/h)', '英里/时 (mph)', '节 (kn)', '马赫 (Ma)'],
    '时间': ['秒 (s)', '分 (min)', '时 (h)', '天 (d)', '周 (wk)', '月', '年'],
    '质量': ['克 (g)', '千克 (kg)', '毫克 (mg)', '吨 (t)', '盎司 (oz)', '磅 (lb)'],
    '面积': ['平方米 (m²)', '平方千米 (km²)', '平方厘米 (cm²)', '公顷 (ha)', '亩', '平方英尺 (ft²)', '英亩 (ac)'],
    '体积': ['升 (L)', '毫升 (mL)', '立方米 (m³)', '立方厘米 (cm³)', '加仑 (gal)', '品脱 (pt)'],
    '压强': ['帕斯卡 (Pa)', '千帕 (kPa)', '兆帕 (MPa)', '巴 (bar)', '标准大气压 (atm)', '毫米汞柱 (mmHg)', '磅/平方英寸 (psi)'],
    '电压': ['伏特 (V)', '千伏 (kV)', '毫伏 (mV)', '微伏 (μV)'],
    '进制': ['二进制', '八进制', '十进制', '十六进制'],
  };

  /// 转换值
  static String? convert(String category, String from, String to, String input) {
    if (input.isEmpty) return null;

    // 进制换算特殊处理
    if (category == '进制') {
      return _convertRadix(from, to, input);
    }

    // 数值换算
    final value = double.tryParse(input);
    if (value == null) return null;

    final result = _convertValue(value, category, from, to);
    return _formatResult(result);
  }

  /// 进制换算
  static String? _convertRadix(String from, String to, String input) {
    try {
      int intValue;
      if (from == '二进制') {
        intValue = int.parse(input, radix: 2);
      } else if (from == '八进制') {
        intValue = int.parse(input, radix: 8);
      } else if (from == '十进制') {
        intValue = int.parse(input);
      } else if (from == '十六进制') {
        intValue = int.parse(input, radix: 16);
      } else {
        intValue = int.parse(input);
      }

      if (to == '二进制') {
        return intValue.toRadixString(2);
      } else if (to == '八进制') {
        return intValue.toRadixString(8);
      } else if (to == '十进制') {
        return intValue.toString();
      } else if (to == '十六进制') {
        return intValue.toRadixString(16).toUpperCase();
      } else {
        return intValue.toString();
      }
    } catch (e) {
      return null;
    }
  }

  /// 数值换算
  static double _convertValue(double value, String category, String from, String to) {
    // 温度换算
    if (category == '温度') {
      double celsius;
      // 先转成摄氏度
      if (from.contains('摄氏')) {
        celsius = value;
      } else if (from.contains('华氏')) {
        celsius = (value - 32) * 5 / 9;
      } else if (from.contains('开尔文')) {
        celsius = value - 273.15;
      } else {
        celsius = value;
      }

      // 再从摄氏度转成目标单位
      if (to.contains('摄氏')) {
        return celsius;
      } else if (to.contains('华氏')) {
        return celsius * 9 / 5 + 32;
      } else if (to.contains('开尔文')) {
        return celsius + 273.15;
      }
      return celsius;
    }

    // 其他单位：使用基准单位换算
    final factors = _getConversionFactors(category);
    final baseValue = value * (factors[from] ?? 1);
    return baseValue / (factors[to] ?? 1);
  }

  /// 获取换算因子
  static Map<String, double> _getConversionFactors(String category) {
    switch (category) {
      case '长度':
        return {
          '米 (m)': 1,
          '千米 (km)': 1000,
          '厘米 (cm)': 0.01,
          '毫米 (mm)': 0.001,
          '英寸 (in)': 0.0254,
          '英尺 (ft)': 0.3048,
          '英里 (mi)': 1609.344,
          '海里 (nmi)': 1852,
        };
      case '速度':
        return {
          '米/秒 (m/s)': 1,
          '千米/时 (km/h)': 1 / 3.6,
          '英里/时 (mph)': 0.44704,
          '节 (kn)': 0.514444,
          '马赫 (Ma)': 340.29,
        };
      case '时间':
        return {
          '秒 (s)': 1,
          '分 (min)': 60,
          '时 (h)': 3600,
          '天 (d)': 86400,
          '周 (wk)': 604800,
          '月': 2592000,
          '年': 31536000,
        };
      case '质量':
        return {
          '克 (g)': 0.001,
          '千克 (kg)': 1,
          '毫克 (mg)': 0.000001,
          '吨 (t)': 1000,
          '盎司 (oz)': 0.0283495,
          '磅 (lb)': 0.453592,
        };
      case '面积':
        return {
          '平方米 (m²)': 1,
          '平方千米 (km²)': 1000000,
          '平方厘米 (cm²)': 0.0001,
          '公顷 (ha)': 10000,
          '亩': 666.667,
          '平方英尺 (ft²)': 0.092903,
          '英亩 (ac)': 4046.86,
        };
      case '体积':
        return {
          '升 (L)': 0.001,
          '毫升 (mL)': 0.000001,
          '立方米 (m³)': 1,
          '立方厘米 (cm³)': 0.000001,
          '加仑 (gal)': 0.00378541,
          '品脱 (pt)': 0.000473176,
        };
      case '压强':
        return {
          '帕斯卡 (Pa)': 1,
          '千帕 (kPa)': 1000,
          '兆帕 (MPa)': 1000000,
          '巴 (bar)': 100000,
          '标准大气压 (atm)': 101325,
          '毫米汞柱 (mmHg)': 133.322,
          '磅/平方英寸 (psi)': 6894.76,
        };
      case '电压':
        return {
          '伏特 (V)': 1,
          '千伏 (kV)': 1000,
          '毫伏 (mV)': 0.001,
          '微伏 (μV)': 0.000001,
        };
      default:
        return {};
    }
  }

  /// 格式化结果
  static String _formatResult(double value) {
    if (value == value.toInt()) {
      return value.toInt().toString();
    } else if (value.abs() < 0.0001 || value.abs() > 1000000) {
      return value.toStringAsExponential(6);
    } else {
      return value.toStringAsFixed(6).replaceAll(RegExp(r'0+$'), '').replaceAll(RegExp(r'\.$'), '');
    }
  }
}
