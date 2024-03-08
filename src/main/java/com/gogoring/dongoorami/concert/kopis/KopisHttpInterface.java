package com.gogoring.dongoorami.concert.kopis;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface KopisHttpInterface {

    @GetExchange
    String findAll(@RequestParam String service, @RequestParam String stdate,
            @RequestParam String eddate, @RequestParam Integer cpage, @RequestParam Integer rows,
            @RequestParam String newsql);

    @GetExchange("/{kopisId}")
    String findByKopisId(@PathVariable String kopisId, @RequestParam String service,
            @RequestParam String newsql);
}
