package com.chatee.blog.service;

import com.chatee.blog.entities.Post;
import com.chatee.blog.entities.Reply;
import com.chatee.blog.repository.PostRepository;
import com.chatee.blog.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyService {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private PostRepository postRepository;

    // Save a reply for a specific post
    public Reply saveReply(Long postId, Reply reply) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("Post not found with id: " + postId));
        reply.setPost(post);
        return replyRepository.save(reply);
    }

    // Get all replies for a specific post
    public List<Reply> getRepliesByPostId(Long postId) {
        return replyRepository.findByPostId(postId);
    }
}