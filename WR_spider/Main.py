# -*- coding: utf-8 -*-
import codecs
import re
from lxml import etree
import requests


def spider_1():
    r = requests.post(
        "https://zh.wikipedia.org/wiki/%E4%B8%AD%E8%8F%AF%E6%B0%91%E5%9C%8B%E8%87%BA%E7%81%A3%E5%9C%B0%E5%8D%80%E9%84%89%E9%8E%AE%E5%B8%82%E5%8D%80%E5%88%97%E8%A1%A8")
    text = r.text
    html = etree.HTML(text)# Used lxml for xml documents
    result_title = html.xpath('//td/a/@title')#find the name of places
    print len(result_title)
    result_href = html.xpath('//td/a/@href')#find the url of places
    print len(result_href)
    with codecs.open('temp.csv', 'w', 'utf8') as f:#save it .
        for i in range(1, 746):
            f.write(result_title[i] + "," + result_href[i + 1] + "\r\n")
    print ("finish prework")

"""
After spider_1，I delete duplicates by excel,and then save result as temp_1.csv
"""

def spider_2():
    sites = []
    places = []
    num = 0;
    with open('temp_1.csv', 'rb') as f:#read in the result of spider_1
        for i in f.readlines():
            line = i.split("\t")
            places.append(line[0])
            print len(line[1])
            print len(line[1][:len(line[1])-2])# the url are attached with"\n"；so need to delete it
            print "==="
            # if
            sites.append(line[1][:len(line[1])-2])
    print ("finish loading")
    # pattern = re.compile(r'>历史')
    for i in sites:# save the result to a folder named "sites"
        i=i.replace("\n","")
        print i
        site = "https://zh.wikipedia.org" + i
        print site
        r = requests.get(site)
        num += 1
        # print num
        print str(round(num/3.76,3))+"%"
        with codecs.open('sites//'+places[num-1] + '.html', 'w', 'utf8') as f:
            f.write(r.text)


def test():#    Tried to find the content ,but failed
    pass
    # # 将正则表达式编译成Pattern对象
    # site = "https://zh.wikipedia.org/zh-tw/wiki/%E8%87%BA%E5%8D%97%E7%B8%A3"
    # r = requests.get(site)
    # # print type(r.text)
    # str_re = r.text.replace("\n","")
    # print str_re
    # pattern = re.compile('>历史(.*?)<h2>')
    # # # 使用search()查找匹配的子串，不存在能匹配的子串时将返回None
    # # # 这个例子中使用match()无法成功匹配
    # match = re.search(pattern, str_re)
    # if match:
    #     #     # 使用Match获得分组信息
    #     print "===" + match.group(0)
    #     #     ### 输出 ###
    #     #     # world
    #     # html = etree.HTML(r.text)
    #     # result= html.xpath('//h2[contains(,>历史)]')
    #     # print len(result)


if __name__ == "__main__":
    spider_1()#get the name and url of taiwanness cities and districts
    spider_2()# get the websites in wiki and download
    # test()
