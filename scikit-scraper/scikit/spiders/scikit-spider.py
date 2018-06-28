import unicodedata
from scikit.items import MainItem, MethodItem
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor

#to scrape the API documentation page of scikit
class ScikitSpider(CrawlSpider):
    name = "sci-spider"
    start_urls = ['http://scikit-learn.org/stable/modules/classes.html']

    #The xpath in restrict_paths only collects the internal links
    rules = (Rule(LinkExtractor(allow=(), restrict_xpaths=('//td/a[@class="reference internal"]',)),
             callback="parse_item",
             follow=True),)

    @staticmethod
    def parse_item(response):
    	item = MainItem()
        section = response.css("div.section")
        title = section.xpath("dl/dt/code/text()")
        item['libName'] = title.extract_first()[:-1]
        item['funcName'] = title.extract()[-1]
        item['funcDesc'] = unicodedata.normalize('NFKD', section.xpath("dl/dd/p/text()").extract_first().replace('\n', ' ')).encode('ascii','ignore')
        item['notes'] = ' '.join([unicodedata.normalize('NFKD', txt.strip().replace('\n', ' ')).encode('ascii','ignore') for txt in section.xpath("//p[text()='Notes']/following-sibling::p[not(@class='rubric')]//text()").extract()]) #returns text from the follwoing <p> tags as a single string, if the <p> tag is not associated with "rubric" class
        item['methods'] = []

        #for sites that don't have method table and just have a paramTable, check if the mtable is empty, if it is then the first table on the site would be paramTable
        mtable = response.xpath('//table[@class="longtable docutils"]')
        paramTable = []
        methods = []
        if not mtable:
            #if there's no method's table, the only table on page is ParamTable
            paramTable = response.xpath('//table[@class="docutils field-list"]')
        else:
            #table preceding method's table is paramTable
            paramTable = response.xpath('//table[@class="longtable docutils"]/preceding-sibling::table[1]')
            methods = response.xpath('//dl[@class="method"]')

        if paramTable:
            for row in paramTable.xpath('tbody/tr[@class="field-odd field"] | tbody/tr[@class="field-even field"]'):
                fieldname = row.xpath("th/text()")
                #check the value of colHead/fieldname, if it's 'Parameters', 'Attributes' or 'Returns'. Assign accordingly.

                if "Parameters" in fieldname.extract_first():
                    item['allFuncParams'] = row.css("td p strong::text").extract()
                    #extract the entire colBody (without including sentences starting with \n)
                    item['funcParamBody'] = ' '.join([unicodedata.normalize('NFKD', txt.strip().replace('\n', ' ')).encode('ascii','ignore') for txt in row.xpath("td/descendant::text()[not(starts-with(., '\n'))]").extract()])

                if "Attributes" in fieldname.extract_first():
                    item['allFuncAttributes'] = row.css("td p strong::text").extract()
                    item['funcAttrBody'] = ' '.join([unicodedata.normalize('NFKD', txt.strip().replace('\n', ' ')).encode('ascii','ignore') for txt in row.xpath("td/descendant::text()[not(starts-with(., '\n'))]").extract()])

                if "Returns" in fieldname.extract_first():
                    item['allReturnParams'] = row.css("td p strong::text").extract()
                    item['funcReturnBody'] = ' '.join([unicodedata.normalize('NFKD', txt.strip().replace('\n', ' ')).encode('ascii','ignore') for txt in row.xpath("td/descendant::text()[not(starts-with(., '\n'))]").extract()])

        #loop over methods
        if mtable:
            # extract all methods from the page
            for method in methods:
                method_item = MethodItem()
                text = ""
                #to extract the methodName
                for element in method.xpath("dt/descendant::text()[not(parent::a)]").extract():
                    if "\n" not in element and '[source]' not in element:
                        text = text + ''.join(unicodedata.normalize('NFKD', element.replace('\n', ' ')).encode('ascii','ignore'))
                method_item['methodName'] = text
                method_item['methodDesc'] = ' '.join([unicodedata.normalize('NFKD', txt.strip().replace('\n', ' ')).encode('ascii','ignore') for txt in method.xpath("dd/p//text()").extract()])

                #extract the table inside "method" class
                innerMethodTable = method.xpath("dd/table[@class='docutils field-list']")

                if innerMethodTable:
                    for row in innerMethodTable.xpath('tbody/tr[@class="field-odd field"] | tbody/tr[@class="field-even field"]'):
                        fieldname = row.xpath("th/text()")
                        if "Parameters" in fieldname.extract_first():
                            method_item['methodParams'] = row.css("td p strong::text").extract()
                            method_item['methodParamsBody'] = ' '.join([unicodedata.normalize('NFKD', txt.strip().replace('\n', ' ')).encode('ascii','ignore') for txt in row.xpath("td//text()").extract()])
                        if "Returns" in fieldname.extract_first():
                            method_item['methodReturns'] = row.css("td p strong::text").extract()
                            method_item['methodReturnsBody'] = ' '.join([unicodedata.normalize('NFKD', txt.strip().replace('\n', ' ')).encode('ascii','ignore') for txt in row.xpath("td//text()").extract()])
                item['methods'].append(dict(method_item))

        yield item



