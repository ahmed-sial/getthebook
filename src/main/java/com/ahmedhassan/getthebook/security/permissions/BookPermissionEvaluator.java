package com.ahmedhassan.getthebook.security.permissions;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ahmedhassan.getthebook.entities.Book;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.repositories.BookRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookPermissionEvaluator implements PermissionEvaluator {
  private final BookRepository _bookRepository;

  @Override
  public boolean hasPermission(
      Authentication authentication,
      Object targetDomainObject,
      Object permission) {

    // used when have access to object in hand
    if (targetDomainObject instanceof Book book) {
      User user = (User) authentication.getPrincipal();
      return book.getUser().getId().equals(user.getId());
    }
    return false;
  }

  @Override
  public boolean hasPermission(
      Authentication authentication,
      Serializable targetId,
      String targetType,
      Object permission) {

    // used when only have access to ID
    if ("Book".equals(targetType)) {
      User user = (User) authentication.getPrincipal();
      return switch ((String) permission) {
        case "READ" -> _bookRepository.findById((UUID) targetId)
            .map(book -> book.getIsShareable() && !book.getIsArchived() || isOwner(user, book))
            .orElse(false);

        case "UPDATE", "DELETE" -> _bookRepository.findById((UUID) targetId)
            .map(book -> isOwner(user, book))
            .orElse(false);
        default -> false;
      };
    }
    return false;
  }

  private boolean isOwner(User user, Book book) {
    return book.getUser().getId().equals(user.getId());
  }
}