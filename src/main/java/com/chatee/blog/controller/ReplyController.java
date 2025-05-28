package com.chatee.blog.controller;

import com.chatee.blog.entities.Reply;
import com.chatee.blog.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/replies")
@CrossOrigin(origins = "http://localhost:3000")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @PostMapping
    public Reply addReply(@PathVariable Long postId, @RequestBody Reply reply) {
        return replyService.saveReply(postId, reply);
    }

    @GetMapping
    public List<Reply> getReplies(@PathVariable Long postId) {
        return replyService.getRepliesByPostId(postId);
    }
}