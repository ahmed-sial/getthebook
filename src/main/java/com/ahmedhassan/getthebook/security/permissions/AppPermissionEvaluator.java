package com.ahmedhassan.getthebook.security.permissions;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ahmedhassan.getthebook.entities.Book;
import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.repositories.BookRepository;
import com.ahmedhassan.getthebook.repositories.BookShareAppealRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppPermissionEvaluator implements PermissionEvaluator {
  private final BookRepository _bookRepository;
  private final BookShareAppealRepository _bookShareAppealRepository;

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
    var user = (User) authentication.getPrincipal();
    // used when have access to object in hand
    if (targetDomainObject instanceof Book book) {
      return isOwner(user, book);
    }
    if (targetDomainObject instanceof BookShareAppeal appeal)
      return appeal.getUser().getId().equals(user.getId());

    return false;
  }

  @Override
  public boolean hasPermission(Authentication authentication,
      Serializable targetId,
      String targetType,
      Object permission) {
    User user = (User) authentication.getPrincipal();

    return switch (targetType) {

      case "Book" -> switch ((String) permission) {
        case "READ" -> _bookRepository.findById((UUID) targetId)
            .map(book -> (book.getIsShareable() && !book.getIsArchived()) || isOwner(user, book))
            .orElse(false);
        case "PATCH", "DELETE", "UPDATE" -> _bookRepository.findById((UUID) targetId)
            .map(book -> isOwner(user, book))
            .orElse(false);
        default -> false;
      };

      case "BookShareAppeal" -> switch ((String) permission) {
        case "READ" -> _bookShareAppealRepository.findById((UUID) targetId)
            .map(appeal -> (appeal.getUser().getId().equals(user.getId())
                || (appeal.getBook().getUser().getId().equals(user.getId()))))
            .orElse(false);

        case "DELETE" -> _bookShareAppealRepository.findById((UUID) targetId)
            .map(appeal -> appeal.getUser().getId().equals(user.getId()))
            .orElse(false);

        case "APPROVE", "REJECT" -> _bookShareAppealRepository.findById((UUID) targetId)
            .map(appeal -> appeal.getBook().getUser().getId().equals(user.getId()))
            .orElse(false);
        default -> false;
      };

      default -> false;
    };
  }

  private boolean isOwner(User user, Book book) {
    return book.getUser().getId().equals(user.getId());
  }
}