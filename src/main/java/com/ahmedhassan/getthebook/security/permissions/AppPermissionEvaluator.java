package com.ahmedhassan.getthebook.security.permissions;

import com.ahmedhassan.getthebook.repositories.BookShareRepository;
import java.io.Serializable;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ahmedhassan.getthebook.entities.Book;
import com.ahmedhassan.getthebook.entities.BookShare;
import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.repositories.BookRepository;
import com.ahmedhassan.getthebook.repositories.BookShareAppealRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppPermissionEvaluator implements PermissionEvaluator {
  private final BookShareRepository _bookShareRepository;
  private final BookRepository _bookRepository;
  private final BookShareAppealRepository _bookShareAppealRepository;

  @Override
  public boolean hasPermission(@NonNull Authentication authentication, @NonNull Object targetDomainObject, @NonNull Object permission) {
    var user = (User) authentication.getPrincipal();
    if (user == null) {
      return false;
    }
    // used when have access to object in hand
	  return switch (targetDomainObject) {
		  case Book book -> isOwner(user, book);
		  case BookShareAppeal appeal -> appeal.getUser().getId().equals(user.getId());
		  case BookShare bookShare -> bookShare.getBook().getUser().getId().equals(user.getId());
		  default -> false;
	  };

  }

  @Override
  public boolean hasPermission(@NonNull Authentication authentication,
                               @NonNull Serializable targetId,
                               @NonNull String targetType,
                               @NonNull Object permission) {
    User user = (User) authentication.getPrincipal();
    if (user == null) {
      return false;
    }
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

      case "BookShare" -> switch ((String) permission) {
        case "READ" -> _bookShareRepository.findById((UUID) targetId)
        .map(share -> share.getBook().getUser().getId().equals(user.getId()))
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