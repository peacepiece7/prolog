import * as Styled from './Article.style';
import thumbnailExample from '../../assets/images/article-ex.png';
import type { ArticleType } from '../../models/Article';
import styled from '@emotion/styled';

const Article = ({ id, title, userName, url, createdAt }: ArticleType) => {
  const createdDate = createdAt.split('-').join('. ');

  return (
    <Styled.Container>
      <Anchor href={url} target="_blank" rel="noopener noreferrer">
        <Styled.ThumbnailWrapper>
          <Styled.Thumbnail src={thumbnailExample} />
        </Styled.ThumbnailWrapper>
        <Styled.ArticleInfoContainer>
          <Styled.UserName>{userName}</Styled.UserName>
          <Styled.Title>{title}</Styled.Title>
          <Styled.CreatedAt>{createdDate}</Styled.CreatedAt>
        </Styled.ArticleInfoContainer>
      </Anchor>
    </Styled.Container>
  );
};

export default Article;

export const Anchor = styled.a``;
