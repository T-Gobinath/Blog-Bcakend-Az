package com.chatee.blog.service;

import com.chatee.blog.entities.Post;
import com.chatee.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List; // <-- Add this import

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    // Add this method
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}