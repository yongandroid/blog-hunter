package me.zhyd.hunter.resolver;

import me.zhyd.hunter.config.HunterConfig;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

import java.util.Arrays;

/**
 * 解析处理普通的Html网页
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/7/31 17:37
 */
public class HtmlResolver implements Resolver {

    @Override
    public void process(Page page, HunterConfig model) {
        Html pageHtml = page.getHtml();
        String title = pageHtml.xpath(model.getTitleRegex()).get();
        String source = page.getRequest().getUrl();
        if (model.isSingle() || (!StringUtils.isEmpty(title) && (!"null".equals(title) && !model.getEntryUrls().contains(source)))) {
            page.putField("title", title);
            page.putField("source", source);
            this.put(page, pageHtml, "releaseDate", model.getReleaseDateRegex());
            this.put(page, pageHtml, "author", model.getAuthorRegex());
            this.put(page, pageHtml, "content", model.getContentRegex());
            this.put(page, pageHtml, "tags", model.getTagRegex());
            this.put(page, pageHtml, "description", model.getDescriptionRegex());
            this.put(page, pageHtml, "keywords", model.getKeywordsRegex());
        }
        if (!model.isSingle()) {
            page.addTargetRequests(page.getHtml().links().regex(model.getTargetLinksRegex()).all());
        }
    }

    private void put(Page page, Html pageHtml, String key, String regex) {
        if (StringUtils.isNotEmpty(regex)) {
            if(key.equals("tags")) {
                page.putField(key, pageHtml.xpath(regex).all());
                return;
            }
            page.putField(key, pageHtml.xpath(regex).get());
        }
    }
}
