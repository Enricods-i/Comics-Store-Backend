package im.enricods.ComicsStore.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import im.enricods.ComicsStore.repositories.CollectionRepository;
import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;

@Service
public class ResearchCollectionsService {
    
    @Autowired
    private CollectionRepository collectionRepository;

    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByName(String name, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByNameContaining(name,paging);
        return pagedResult.getContent();
    }//showCollectionByName

    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByCategory(Category category, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByCategory(category, paging);
        return pagedResult.getContent();
    }//showCollectionByCategory

    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByAuthor(Author author, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByAuthor(author, paging);
        return pagedResult.getContent();
    }//showCollectionsByAuthor

}//ResearchService
