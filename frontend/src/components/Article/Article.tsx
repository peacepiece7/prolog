import * as Styled from './Article.style';
import thumbnailExample from '../../assets/images/article-ex.png';
import type { ArticleType } from '../../models/Article';

const Article = ({ id, title, userName, url, createdAt }: ArticleType) => {
  return (
    <Styled.Container>
      <Styled.Anchor href={url} target="_blank" rel="noopener noreferrer">
        <Styled.ThumbnailWrapper>
          <Styled.Thumbnail src={thumbnailExample} />
        </Styled.ThumbnailWrapper>
        <Styled.ArticleInfoContainer>
          <Styled.UserName>{userName}</Styled.UserName>
          <Styled.Title>{title}</Styled.Title>
          <Styled.CreatedAt>{createdAt}</Styled.CreatedAt>
        </Styled.ArticleInfoContainer>
      </Styled.Anchor>
    </Styled.Container>
  );
};

export default Article;
