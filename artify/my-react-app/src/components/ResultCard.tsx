import Star from "./bookmark";
import axios from "axios";
import { useAuth } from "../AuthContext.tsx";
import { Card } from "react-bootstrap";

function ResultCard(props: any) {
  const searchResult = props.searchResult?._embedded?.results ?? [];
  const { userData } = useAuth();

  function handleClick(id: string, img: string) {
    props.setShowWiki(true);

    const req1 = axios.post(
      "https://hw3-cs571-spring-25.uw.r.appspot.com/api/about",
      { uid: id }
    );
    const req2 = axios.post(
      "https://hw3-cs571-spring-25.uw.r.appspot.com/api/artworks",
      { uid: id }
    );

    Promise.all([req1, req2])
      .then(([res1, res2]) => {
        props.setArtistWiki({ ...res1.data, img });
        props.setArtworks(res2.data);
      })
      .catch((err) => {
        console.log("Error while requesting artist wiki or artworks", err);
      });

    if (userData) {
      axios
        .post("https://hw3-cs571-spring-25.uw.r.appspot.com/api/similar", {
          uid: id,
        })
        .then((res) => {
          props.setSimilarArtists(res.data);
        })
        .catch((err) => {
          console.log("Error while sending similar artist query", err);
        });
    }
  }

  function createCard(cardItem: any) {
    let artist_uid = "";
    let artist_name = "";
    let artist_img = "";

    try {
      artist_uid = cardItem?._links?.self?.href.substr(34) ?? null;
      artist_name = cardItem?.title ?? "artist_name not found";
      artist_img = cardItem?._links?.thumbnail?.href ?? "/artsy_logo.svg";
      if (artist_img === "/assets/shared/missing_image.png") {
        artist_img = "/artsy_logo.svg";
      }
    } catch (err) {
      console.log("Error while retrieving artist information", err);
    }
    return (
      <Card
        className="card"
        key={artist_uid}
        onClick={() => handleClick(artist_uid, artist_img)}
      >
        <Star type="card" id={artist_uid} img={artist_img} />
        <Card.Img variant="top" src={artist_img} />
        <Card.Footer>{artist_name}</Card.Footer>
      </Card>
    );
  }

  return <div className="scroller ">{searchResult.map(createCard)}</div>;
}

export default ResultCard;
