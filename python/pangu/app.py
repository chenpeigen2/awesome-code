import csv
from pangu import header
import pandas as pd
from io import StringIO


def csv_to_xls(csv_path, xls_path):
    with open(csv_path) as f:
        data = f.read()
    data_file = StringIO(data)
    print(data_file)
    csv_reader = csv.reader(data_file)
    list_csv = []
    for row in csv_reader:
        list_csv.append(row)
    df_csv = pd.DataFrame(list_csv).applymap(str)
    '''
    这部分是不将csv装换为xls，而是过滤后再写入csv文件
    df_csv = df_csv[(df_csv[4] == '') | (df_csv[4] == 'name')]      # 过滤出第四列包含空值和name的数据
    df_csv.to_csv(csv_path, index=0, header=0, encoding='gb18030')  # 写入csv文件中
    '''
    writer = pd.ExcelWriter(xls_path)
    # 写入Excel
    df_csv.to_excel(
        excel_writer=writer,
        index=False,
        header=False
    )

    writer.save()


def pretty():
    with open('pretty.csv', 'w') as f1:
        fieldnames = ['模块名', '是否条件编译', '条件编译维度', '风险的条件编译', '分区', 'hide  api + unsupported', '交付手段', '条件编译', '条件编译源码',
                      'region', '机型']
        fw_csv = csv.DictWriter(f1, fieldnames=fieldnames)
        fw_csv.writeheader()
        with open('gather.csv') as f:
            f_csv = csv.DictReader(f)
            for row in f_csv:
                v = row['分区1']
                print(row['分区'], row['分区1'])
                if v == '':
                    v = row['分区']

                # print(row['模块名'])
                # print(row['是否条件编译'])
                # print(row['条件编译维度'])
                # print(row['风险的条件编译'])

                fw_csv.writerow(
                    {'模块名': row['模块名'], '是否条件编译': row['是否条件编译'], '条件编译维度': row['条件编译维度'], '风险的条件编译': row['风险的条件编译'],
                     '分区': v,
                     'hide  api + unsupported': row['hide  api + unsupported'],
                     '交付手段': row['交付手段'], '条件编译': row['条件编译'], '条件编译源码': row['条件编译源码'], 'region': row['region'],
                     '机型': row['机型']
                     })
    csv_to_xls('pretty.csv', 'pretty.xlsx')


if __name__ == '__main__':
    s1 = set()
    l1 = []
    with open('basic.csv') as f:
        f_csv = csv.reader(f)
        next(f_csv)  # pass the first column
        for row in f_csv:
            v = row[0].strip()
            s1.add(v)
            l1.append(v)

    s2 = set()
    l2 = []
    with open('advanced.csv') as f:
        f_csv = csv.reader(f)
        next(f_csv)  # pass the first column
        for row in f_csv:
            v = row[header.index('模块名')].strip()
            s2.add(v)
            l2.append(v)

    # # a = [i for i in s1 if i not in s2]  # s1固有的
    # a1 = [i for i in s1 if i in s2]
    # # b = [i for i in s2 if i not in s1]  # s2 固有的
    # b1 = [i for i in s2 if i in s1]
    # # print(a)
    # print(a1)
    #
    # # print(b)
    # print(b1)

    v1 = s1 & s2
    v2 = s1 ^ s2
    v3 = s1 - s2
    v4 = s2 - s1

    mp = {}

    with open('basic.csv') as f:
        f_csv = csv.reader(f)
        next(f_csv)  # pass the first column
        for row in f_csv:
            v = row[0].strip()
            if v in v1:
                mp[v] = row

    with open('a&b.csv', 'w') as f1:
        fw_csv = csv.writer(f1)
        with open('advanced.csv') as f:
            f_csv = csv.reader(f)
            next(f_csv)  # pass the first column
            fw_csv.writerow(header)
            for row in f_csv:
                v = row[header.index('模块名')].strip()
                if v in v1:
                    p1 = row[0:header.index('region')]
                    p2 = mp[v][4:8]
                    print(header.index('是否条件编译'))
                    p3 = row[28:-1]
                    p = p1 + p2 + p3
                    fw_csv.writerow(p)

    with open('a.csv', 'w') as f1:
        fw_csv = csv.writer(f1)
        with open('basic.csv') as f:
            f_csv = csv.reader(f)
            next(f_csv)  # pass the first column
            fw_csv.writerow(header)
            for row in f_csv:
                v = row[0].strip()
                if v in v3:
                    p1 = ['' for i in range(header.index('模块名'))]
                    p2 = [v]
                    p3 = ['' for i in range(header.index('region') - header.index('分区'))]
                    p4 = row[4:8]
                    p5 = ['' for i in range(len(header) - header.index('是否条件编译'))]
                    p = p1 + p2 + p3 + p4 + p5
                    fw_csv.writerow(p)

    with open('b.csv', 'w') as f1:
        fw_csv = csv.writer(f1)
        with open('advanced.csv') as f:
            f_csv = csv.reader(f)
            next(f_csv)  # pass the first column
            fw_csv.writerow(header)
            for row in f_csv:
                v = row[header.index('模块名')].strip()
                if v in v4:
                    p1 = row[0:header.index('region')]
                    p2 = ['' for i in range(4)]
                    p3 = row[28:-1]
                    p = p1 + p2 + p3
                    fw_csv.writerow(p)

    # format a standard output
    with open('gather.csv', 'w') as f1:
        fw_csv = csv.writer(f1)
        fw_csv.writerow(header)
        with open('a&b.csv') as f:
            f_csv = csv.reader(f)
            next(f_csv)
            for row in f_csv:
                fw_csv.writerow(row)

        with open('a.csv') as f:
            f_csv = csv.reader(f)
            next(f_csv)
            for row in f_csv:
                fw_csv.writerow(row)

        with open('b.csv') as f:
            f_csv = csv.reader(f)
            next(f_csv)
            for row in f_csv:
                fw_csv.writerow(row)

    csv_to_xls('gather.csv', 'gather.xlsx')
    csv_to_xls('a.csv', 'a.xlsx')
    csv_to_xls('b.csv', 'b.xlsx')
    csv_to_xls('a&b.csv', 'a&b.xlsx')

    pretty()
