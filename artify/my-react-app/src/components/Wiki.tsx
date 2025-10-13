import { useState, useEffect } from "react";
import axios from "axios";
import Star from "./bookmark";
import { useAuth } from "../AuthContext.tsx";
import {
  ToggleButtonGroup,
  ToggleButton,
  Card,
  Alert,
  Row,
  Col,
  Modal,
  Image,
  // Spinner,
} from "react-bootstrap";

function Wiki(props: any) {
  const [show, setShow] = useState(1);
  const [showModal, setShowModal] = useState(false);
  const [modalData, setModalData] = useState({
    id: "",
    name: "",
    year: "",
    img: "",
  });

  type Gene = {
    id: string;
    name: string;
    img: string;
  };
  const [modalGenes, setModalGenes] = useState<Gene[]>([]);

  const wiki = props.artistWiki ?? [];
  const artworks = props.artworks?._embedded?.artworks ?? [];
  const similarArtists = props.similarArtists?._embedded?.artists ?? [];
  const { userData } = useAuth();

  let artist = {
    id: wiki?.id ?? null,
    name: wiki?.name ?? null,
    birthday: wiki?.birthday ?? null,
    deathday: wiki?.deathday ?? null,
    nationality: wiki?.nationality ?? null,
    biography: wiki?.biography ?? null,
    img: wiki.img ?? null,
  };

  function handleChange(val: any) {
    setShow(val);
  }

  function handleClick(event: any) {
    const { id, img, name, year } = event;

    setModalData({ id, img, name, year });

    axios
      .post("https://hw3-cs571-spring-25.uw.r.appspot.com/api/category", {
        id: id,
      })
      .then((res) => {
        let categories = res.data?._embedded?.genes ?? [];

        const category =
          categories.map((item: any) => ({
            id: item.id,
            name: item.name,
            img: item._links?.thumbnail?.href ?? "/artsy_logo.svg",
          })) ?? [];
        setModalGenes(category);

        setShowModal(true);
      })
      .catch((err) => {
        console.log("Error while sending search query", err);
      });
  }

  function handleHeadingClick() {
    props.setShowCaraousel(false);
  }
  function createCard(cardItem: any) {
    let artwork = {
      id: "",
      name: "",
      year: "",
      img: "",
    };

    try {
      artwork.id = cardItem?.id ?? null;
      artwork.name = cardItem?.title ?? null;
      artwork.img = cardItem?._links?.thumbnail?.href ?? "/artsy_logo.svg";
      artwork.year = cardItem?.date ?? null;
    } catch (err) {
      console.log("Error while retrieving artwork", err);
    }

    return (
      <Col key={artwork.id}>
        <Card
          className="artworkCard"
          key={artwork.id}
          onClick={() => handleClick(artwork)}
        >
          <Card.Img variant="top" src={artwork.img} />
          <Card.Text>
            {artwork.name},{artwork.year}
          </Card.Text>
          <Card.Footer>View Categories</Card.Footer>
        </Card>
      </Col>
    );
  }

  function createGenes(gene: any) {
    return (
      <Col key={gene.id}>
        <Card className="mb-2" key={gene.id}>
          <Card.Img variant="top" src={gene.img} />
          <Card.Text> {gene.name}</Card.Text>
        </Card>
      </Col>
    );
  }

  function handleSimilarClick(id: string, img: string) {
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

  function createSimilar(cardItem: any) {
    let artist_uid = "";
    let artist_name = "";
    let artist_img = "";

    try {
      artist_uid = cardItem?.id ?? null;
      artist_name = cardItem?.name ?? null;
      artist_img = cardItem?._links?.thumbnail?.href ?? "/artsy_logo.svg";
      if (artist_img === "/assets/shared/missing_image.png") {
        artist_img = "/artsy_logo.svg";
      }
    } catch (err) {
      console.log("Error while retrieving similar artist information", err);
    }
    return (
      <Col key={artist_uid}>
        <Card
          className="similarCard"
          key={artist_uid}
          onClick={() => handleSimilarClick(artist_uid, artist_img)}
        >
          <Star type="card" id={artist_uid} img={artist_img} />
          <Card.Img variant="top" src={artist_img} />
          <Card.Footer>{artist_name}</Card.Footer>
        </Card>
      </Col>
    );
  }

  useEffect(() => {
    const timer = setTimeout(() => {
      props.setInfoSpinner(false);
    }, 2000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="mt-4" style={{ marginBottom: "8rem" }}>
      <ToggleButtonGroup
        className="d-flex mb-4"
        type="radio"
        name="options"
        defaultValue={1}
        onChange={handleChange}
      >
        <ToggleButton
          id="artist_info"
          value={1}
          variant={show === 1 ? "primary" : "outline-secondary"}
        >
          Artist Info
        </ToggleButton>
        <ToggleButton
          id="artworks"
          value={2}
          variant={show === 2 ? "primary" : "outline-secondary"}
        >
          Artworks
        </ToggleButton>
      </ToggleButtonGroup>
      {/* {props.infoSpinner ? (
        <div
          className="d-flex justify-content-center align-items-center"
          style={{ height: "100vh" }}
        >
          <Spinner
            as="span"
            animation="border"
            size="sm"
            role="status"
            aria-hidden="true"
            className="mx-1 infoSpinner"
          />
        </div>
      ) : (
        <> */}
      {show === 1 && (
        <div>
          <div
            className="d-flex flex-column align-items-center justify-content-center"
            style={{ textAlign: "center" }}
          >
            <div className="d-flex align-items-center">
              <h2 onClick={handleHeadingClick}>{artist.name} </h2>
              <div style={{ position: "relative" }}>
                <Star id={artist.id} type="header" img={artist.img} />
              </div>
            </div>
            <h6>
              {artist.nationality && `${artist.nationality}`}
              {artist.nationality && artist.birthday && `, `}
              {artist.birthday && `${artist.birthday}`}
              {artist.birthday && artist.deathday && `-`}
              {artist.deathday && `${artist.deathday}`}
            </h6>
          </div>
          <p>{artist.biography}</p>
          {userData && (
            <div>
              <h2>Similar Artists</h2>
              <Row xs={1} sm={2} md={5} className="g-1">
                {similarArtists.map(createSimilar)}
              </Row>
            </div>
          )}
        </div>
      )}
      {show === 2 && (
        <div>
          <Row xs={1} sm={2} md={4} className="g-4">
            {artworks.map(createCard)}
          </Row>
        </div>
      )}
      {show === 2 && artworks.length === 0 && (
        <Alert className="mt-2" variant="danger">
          No artworks.
        </Alert>
      )}

      <Modal size="xl" show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            <Image src={modalData.img} width="40" height="50" />
            {modalData.name}
            {modalData.year}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalGenes.length === 0 && (
            <Alert variant="danger">No categories.</Alert>
          )}
          <Row xs={1} sm={2} md={4} className="g-4">
            {modalGenes.map(createGenes)}
          </Row>
        </Modal.Body>
      </Modal>
    </div>
  );
}

export default Wiki;
